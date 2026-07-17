package com.englishapp.voice;

public interface VoiceService {
    byte[] textToSpeech(String text);
    String speechToText(byte[] audioData);
}
