#!/usr/bin/env python3
"""
童声中文短语音识别实测：百度（dev_pid=1537）与阿里云百炼（qwen3-asr-flash）各跑一遍。

用法：
    export BAIDU_API_KEY=... BAIDU_SECRET_KEY=...      # 跑百度需要
    export DASHSCOPE_API_KEY=...                        # 跑百炼需要
    # 可选：DASHSCOPE_ASR_ENDPOINT 覆盖为工作空间域名（默认北京 dashscope.aliyuncs.com）
    python3 scripts/asr-eval/asr_eval.py <样本目录> [-o result.csv] [-p baidu,dashscope]

样本目录：每条样本是同名的 a.wav + a.txt（txt 为人工标注的正确文本，UTF-8）。
          wav 需为 16k / 单声道 / 16bit PCM，≤15s（与线上 /agent/asr 一致，不合格的跳过并提示）。
输出：
    CSV 列：文件名, 供应商, 识别文本, CER, 延迟毫秒（失败行识别文本为 ERROR:原因，CER 记 1.0）
    终端打印每家：样本数、失败数、平均 CER、延迟 P50 / P90。
说明：
    - CER = 编辑距离 / 标注字数；比较前两边都去掉标点和空白（与后端 AsrText 一致）。
    - 延迟为单次 HTTP 往返（含建连，百度不含 token 获取），超时：连接 3s / 读取 5s，不重试。
    - 只依赖 Python 3 标准库。音频是儿童语音，结果 CSV 含识别文本，请勿提交到仓库或外传。
"""
import argparse
import base64
import csv
import http.client
import json
import os
import sys
import time
import unicodedata
import urllib.error
import urllib.parse
import urllib.request
import wave
from pathlib import Path

CONNECT_TIMEOUT = 3
READ_TIMEOUT = 5
BAIDU_ASR_URL = "https://vop.baidu.com/server_api"
BAIDU_TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token"
DASHSCOPE_URL = os.environ.get(
    "DASHSCOPE_ASR_ENDPOINT",
    "https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation")


def normalize(s):
    """去掉 Unicode 标点（P* 类）和所有空白。"""
    return "".join(ch for ch in s if not unicodedata.category(ch).startswith("P") and not ch.isspace())


def cer(ref, hyp):
    ref, hyp = normalize(ref), normalize(hyp)
    if not ref:
        return 0.0 if not hyp else 1.0
    prev = list(range(len(hyp) + 1))
    for i, r in enumerate(ref, 1):
        cur = [i] + [0] * len(hyp)
        for j, h in enumerate(hyp, 1):
            cur[j] = min(prev[j] + 1, cur[j - 1] + 1, prev[j - 1] + (r != h))
        prev = cur
    return prev[-1] / len(ref)


def check_wav(path):
    with wave.open(str(path), "rb") as w:
        if (w.getframerate(), w.getnchannels(), w.getsampwidth()) != (16000, 1, 2):
            return "需要 16k/单声道/16bit"
        if w.getnframes() / 16000 > 15:
            return "超过 15s"
    return None


def post_json(url, body, headers=None):
    """返回 (status, json, 耗时ms)。以 3s 超时建连，建连后把 socket 读超时改为 5s。耗时含建连。"""
    u = urllib.parse.urlsplit(url)
    conn_cls = http.client.HTTPSConnection if u.scheme == "https" else http.client.HTTPConnection
    conn = conn_cls(u.hostname, u.port, timeout=CONNECT_TIMEOUT)
    path = u.path + ("?" + u.query if u.query else "")
    start = time.perf_counter()
    try:
        conn.connect()
        conn.sock.settimeout(READ_TIMEOUT)
        conn.request("POST", path, json.dumps(body).encode("utf-8"),
                     {"Content-Type": "application/json", **(headers or {})})
        resp = conn.getresponse()
        status, raw = resp.status, resp.read()
    except OSError as e:  # 含 socket.timeout / 连接失败
        raise RuntimeError(f"网络错误或超时: {e.__class__.__name__}") from e
    finally:
        conn.close()
    ms = int((time.perf_counter() - start) * 1000)
    try:
        return status, json.loads(raw.decode("utf-8") or "{}"), ms
    except ValueError:
        return status, {}, ms


class Baidu:
    name = "baidu"

    def __init__(self):
        ak, sk = os.environ.get("BAIDU_API_KEY"), os.environ.get("BAIDU_SECRET_KEY")
        if not ak or not sk:
            raise RuntimeError("缺少 BAIDU_API_KEY / BAIDU_SECRET_KEY")
        q = urllib.parse.urlencode({"grant_type": "client_credentials", "client_id": ak, "client_secret": sk})
        with urllib.request.urlopen(f"{BAIDU_TOKEN_URL}?{q}", data=b"", timeout=10) as r:
            self.token = json.loads(r.read())["access_token"]

    def recognize(self, wav):
        status, node, ms = post_json(BAIDU_ASR_URL, {
            "format": "wav", "rate": 16000, "channel": 1, "cuid": "english-app-asr-eval",
            "token": self.token, "dev_pid": 1537,
            "speech": base64.b64encode(wav).decode(), "len": len(wav)})
        if status != 200 or node.get("err_no") != 0:
            raise RuntimeError(f"HTTP {status} err_no={node.get('err_no')} {node.get('err_msg', '')}")
        return (node.get("result") or [""])[0], ms


class DashScope:
    name = "dashscope"

    def __init__(self):
        self.key = os.environ.get("DASHSCOPE_API_KEY")
        if not self.key:
            raise RuntimeError("缺少 DASHSCOPE_API_KEY")

    def recognize(self, wav):
        status, node, ms = post_json(DASHSCOPE_URL, {
            "model": "qwen3-asr-flash",
            "input": {"messages": [{"role": "user", "content": [
                {"audio": "data:audio/wav;base64," + base64.b64encode(wav).decode()}]}]},
            "parameters": {"asr_options": {"language": "zh", "enable_itn": True}},
        }, {"Authorization": f"Bearer {self.key}"})
        if status != 200:
            raise RuntimeError(f"HTTP {status} {node.get('code', '')} {node.get('message', '')}")
        content = node["output"]["choices"][0]["message"]["content"]
        return (content[0].get("text", "") if content else ""), ms


def percentile(values, p):
    """最近秩法（nearest-rank）。"""
    if not values:
        return None
    s = sorted(values)
    k = max(1, -(-p * len(s) // 100))
    return s[int(k) - 1]


def main():
    ap = argparse.ArgumentParser(description="中文短语音识别两家实测（CER + 延迟）")
    ap.add_argument("dir", type=Path)
    ap.add_argument("-o", "--output", type=Path, default=Path("asr_eval_result.csv"))
    ap.add_argument("-p", "--providers", default="baidu,dashscope")
    args = ap.parse_args()

    samples = []
    for wav_path in sorted(args.dir.glob("*.wav")):
        txt = wav_path.with_suffix(".txt")
        if not txt.exists():
            print(f"跳过 {wav_path.name}：缺少 {txt.name}", file=sys.stderr)
            continue
        bad = check_wav(wav_path)
        if bad:
            print(f"跳过 {wav_path.name}：{bad}", file=sys.stderr)
            continue
        samples.append((wav_path.name, wav_path.read_bytes(), txt.read_text("utf-8").strip()))
    if not samples:
        sys.exit("没有可用样本")

    classes = {"baidu": Baidu, "dashscope": DashScope}
    rows, summary = [], []
    for name in [p.strip() for p in args.providers.split(",") if p.strip()]:
        try:
            client = classes[name]()
        except Exception as e:  # 缺密钥等：整家跳过
            print(f"[{name}] 跳过：{e}", file=sys.stderr)
            continue
        latencies, cers, fails = [], [], 0
        for fname, wav, ref in samples:
            try:
                text, ms = client.recognize(wav)
                text = normalize(text)
                if not text:
                    raise RuntimeError("识别结果为空")
                c = cer(ref, text)
                latencies.append(ms)
            except (RuntimeError, KeyError, IndexError, TypeError) as e:
                text, c, ms, fails = f"ERROR:{e}", 1.0, "", fails + 1
            cers.append(c)
            rows.append([fname, name, text, f"{c:.4f}", ms])
            print(f"[{name}] {fname} CER={c:.3f} {ms}ms")
        summary.append((name, len(samples), fails, sum(cers) / len(cers),
                        percentile(latencies, 50), percentile(latencies, 90)))

    with args.output.open("w", newline="", encoding="utf-8-sig") as f:
        w = csv.writer(f)
        w.writerow(["文件名", "供应商", "识别文本", "CER", "延迟毫秒"])
        w.writerows(rows)

    print(f"\n结果已写入 {args.output}")
    print(f"{'供应商':<10}{'样本':>6}{'失败':>6}{'平均CER':>10}{'P50ms':>8}{'P90ms':>8}")
    for name, n, fails, avg, p50, p90 in summary:
        print(f"{name:<10}{n:>6}{fails:>6}{avg:>10.4f}{str(p50):>8}{str(p90):>8}")


if __name__ == "__main__":
    main()
