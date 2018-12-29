@ECHO OFF

FOR %%A IN (%*) DO (
  IF NOT EXIST "%%~nA.mp3" ffmpeg.exe -i %%A "%%~nA.ogg"
)
