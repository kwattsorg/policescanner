package net.kwatts.android.NetScanner.service;

interface IPlaybackService {
  boolean play(String id);
  
  void start();
  void stop();
  void pause();
  boolean isPlaying();
  void seekTo(int pos);
  int getCurrentPosition();
  int getDuration();
  void setLooping(boolean looping);
  void setVolume(float leftVolume, float rightVolume);
}
