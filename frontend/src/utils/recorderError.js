export function getRecorderStartErrorMessage(error, capabilities = {}) {
  if (capabilities.secureContext === false) {
    return '跟读功能需要通过 HTTPS 打开'
  }
  if (capabilities.getUserMedia === false) {
    return '当前浏览器不支持麦克风跟读'
  }

  const name = error?.name || ''
  if (name === 'NotAllowedError' || name === 'SecurityError') {
    return '请在浏览器设置中允许麦克风权限'
  }
  if (name === 'NotFoundError' || name === 'DevicesNotFoundError') {
    return '没有检测到可用麦克风'
  }
  if (name === 'NotReadableError' || name === 'TrackStartError') {
    return '麦克风正被其他应用占用，请关闭后重试'
  }
  return '麦克风启动失败，请稍后重试'
}
