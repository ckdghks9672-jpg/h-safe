$ErrorActionPreference = 'Continue'
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
$outDir = "C:\Users\ckdgh\AndroidStudioProjects\MyApplication\demo"
New-Item -ItemType Directory -Force -Path $outDir | Out-Null

function Dump-Ui {
  & $adb shell uiautomator dump /sdcard/ui.xml 2>$null | Out-Null
  return (& $adb shell cat /sdcard/ui.xml)
}

function Tap-ByText([string]$t) {
  $xml = Dump-Ui
  $esc = [regex]::Escape($t)
  $m = [regex]::Match($xml, "clickable=`"true`"[^>]*bounds=`"\[(\d+),(\d+)\]\[(\d+),(\d+)\]`"[^>]*>[\s\S]{0,600}?text=`"$esc`"")
  if (-not $m.Success) {
    $m = [regex]::Match($xml, "bounds=`"\[(\d+),(\d+)\]\[(\d+),(\d+)\]`"[^>]*clickable=`"true`"[^>]*>[\s\S]{0,600}?text=`"$esc`"")
  }
  if (-not $m.Success) {
    $m = [regex]::Match($xml, "text=`"$esc`"[^>]*bounds=`"\[(\d+),(\d+)\]\[(\d+),(\d+)\]`"")
  }
  if ($m.Success) {
    $x = [int](([int]$m.Groups[1].Value + [int]$m.Groups[3].Value) / 2)
    $y = [int](([int]$m.Groups[2].Value + [int]$m.Groups[4].Value) / 2)
    & $adb shell input tap $x $y
    Write-Host "TAP $t -> $x,$y"
    return $true
  }
  Write-Host "MISS $t"
  return $false
}

function Wait-ForText([string]$t, [int]$tries = 8) {
  for ($i = 0; $i -lt $tries; $i++) {
    $xml = Dump-Ui
    if ($xml -match [regex]::Escape($t)) { return $true }
    Start-Sleep -Milliseconds 700
  }
  return $false
}

function Stop-Record($proc) {
  & $adb shell "pkill -l 2 screenrecord" 2>$null
  & $adb shell "killall -2 screenrecord" 2>$null
  Start-Sleep 4
  try { if ($proc -and -not $proc.HasExited) { Stop-Process -Id $proc.Id -Force -ErrorAction SilentlyContinue } } catch {}
}

function Start-Record([string]$remote) {
  & $adb shell rm -f $remote
  # kill any leftover recorder
  & $adb shell "pkill screenrecord" 2>$null
  Start-Sleep 1
  $p = Start-Process -FilePath $adb -ArgumentList @('shell','screenrecord','--time-limit','180','--bit-rate','12M',$remote) -PassThru -WindowStyle Hidden
  Start-Sleep 4
  return $p
}

function Ensure-App {
  & $adb shell am start --user 10 -n com.example.myapplication/.MainActivity 2>$null | Out-Null
  Start-Sleep 2
}

# ---------- prep: go home then clear ----------
Ensure-App
$xml = Dump-Ui
if ($xml -match '홈' -and $xml -notmatch '설정') {
  Tap-ByText '홈' | Out-Null
  Start-Sleep 2
}
$xml = Dump-Ui
if ($xml -match '설정') {
  Tap-ByText '설정' | Out-Null
  Start-Sleep 2
  Tap-ByText '등록 초기화' | Out-Null
  Start-Sleep 3
}
if (-not (Wait-ForText '샘플 입력' 10)) {
  & $adb shell am force-stop --user 10 com.example.myapplication
  Ensure-App
  Start-Sleep 2
}
if (-not (Wait-ForText '샘플 입력' 8)) {
  Write-Host 'FAIL: cannot reach onboarding'
  exit 1
}
Write-Host 'ONBOARDING_READY'

# ---------- DEMO 1: registration ----------
$remote1 = '/sdcard/Download/hsafe_demo_reg.mp4'
$rec1 = Start-Record $remote1
Start-Sleep 2
Tap-ByText '샘플 입력' | Out-Null
Start-Sleep 3
Tap-ByText '다음' | Out-Null
Start-Sleep 4
# hold on filled page 2
Start-Sleep 2
Tap-ByText '저장하고 시작' | Out-Null
Start-Sleep 4
Stop-Record $rec1
& $adb pull $remote1 "$outDir\hsafe_demo_사용자등록.mp4"
$size1 = (Get-Item "$outDir\hsafe_demo_사용자등록.mp4").Length
Write-Host ("DEMO1_SIZE_BYTES=" + $size1)

if (-not (Wait-ForText '교통사고' 8)) {
  Ensure-App
  Start-Sleep 2
}

# ---------- DEMO 2: traffic -> 119 emergency -> 119 fire ----------
$remote2 = '/sdcard/Download/hsafe_demo_use.mp4'
$rec2 = Start-Record $remote2
Start-Sleep 2

Write-Host 'FLOW traffic'
Tap-ByText '교통사고' | Out-Null
Start-Sleep 3
Tap-ByText '시연 샘플' | Out-Null
Start-Sleep 3
Tap-ByText '후미 추돌' | Out-Null
Start-Sleep 4
Tap-ByText '홈' | Out-Null
Start-Sleep 3

Write-Host 'FLOW 119 emergency'
Tap-ByText '119 응급' | Out-Null
Start-Sleep 3
Tap-ByText '의식 없음' | Out-Null
Start-Sleep 4
Tap-ByText '홈' | Out-Null
Start-Sleep 3

Write-Host 'FLOW 119 fire'
Tap-ByText '119 소방' | Out-Null
Start-Sleep 3
Tap-ByText '차량 화재' | Out-Null
Start-Sleep 5

Stop-Record $rec2
& $adb pull $remote2 "$outDir\hsafe_demo_사고대응.mp4"
$size2 = (Get-Item "$outDir\hsafe_demo_사고대응.mp4").Length
Write-Host ("DEMO2_SIZE_BYTES=" + $size2)
Write-Host 'ALL_DEMO_DONE'
Get-ChildItem "$outDir\hsafe_demo_사용자등록.mp4","$outDir\hsafe_demo_사고대응.mp4" |
  Format-Table Name, @{N='MB';E={[math]::Round($_.Length/1MB,2)}}, LastWriteTime
