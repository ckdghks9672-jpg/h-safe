# -*- coding: utf-8 -*-
from pathlib import Path

screens = Path(r"C:/Users/ckdgh/AndroidStudioProjects/MyApplication/automotive/src/main/java/com/example/myapplication/ui/Screens.kt")
frag = Path(r"C:/Users/ckdgh/AndroidStudioProjects/MyApplication/tools/frag_onboarding.kt").read_text(encoding="utf-8")
text = screens.read_text(encoding="utf-8")
start = text.index("@Composable\nfun OnboardingScreen(")
end = text.index("@Composable\nfun HomeScreen(")
text = text[:start] + frag + text[end:]

# ResponseFlow: add onDial119 param and 119 call buttons
old_sig = """fun ResponseFlowScreen(
    state: AppUiState,
    messagePreview: String,
    insurerPhone: String,
    onBack: () -> Unit,
    onSelectKind: (AccidentKind) -> Unit,
    onUpdateMovableTow: (TriState?, TriState?) -> Unit,
    onDial: () -> Unit,
    onOnline: () -> Unit,
    onMessageIntake: () -> Unit,
    onSend119Emergency: () -> Unit,
    onSend119Fire: () -> Unit,
    onDemo: () -> Unit
)"""
new_sig = """fun ResponseFlowScreen(
    state: AppUiState,
    messagePreview: String,
    insurerPhone: String,
    onBack: () -> Unit,
    onSelectKind: (AccidentKind) -> Unit,
    onUpdateMovableTow: (TriState?, TriState?) -> Unit,
    onDial: () -> Unit,
    onDial119: () -> Unit,
    onOnline: () -> Unit,
    onMessageIntake: () -> Unit,
    onSend119Emergency: () -> Unit,
    onSend119Fire: () -> Unit,
    onDemo: () -> Unit
)"""
if old_sig not in text:
    raise SystemExit("ResponseFlow signature not found")
text = text.replace(old_sig, new_sig)

old_119 = """                    when (state.responseMode) {
                        ResponseMode.EMERGENCY_119 -> BigAction(
                            "119 응급 메시지 보내기",
                            onClick = onSend119Emergency,
                            enabled = selected != AccidentKind.UNKNOWN,
                            container = HSafeColors.EmergencyRed
                        )
                        ResponseMode.FIRE_119 -> BigAction(
                            "119 소방 메시지 보내기",
                            onClick = onSend119Fire,
                            enabled = selected != AccidentKind.UNKNOWN,
                            container = HSafeColors.FireOrange
                        )"""
new_119 = """                    when (state.responseMode) {
                        ResponseMode.EMERGENCY_119 -> {
                            BigAction(
                                "119 전화 연결",
                                onClick = onDial119,
                                container = HSafeColors.EmergencyRed
                            )
                            BigAction(
                                "119 응급 메시지 보내기",
                                onClick = onSend119Emergency,
                                enabled = selected != AccidentKind.UNKNOWN,
                                container = HSafeColors.EmergencyRed
                            )
                        }
                        ResponseMode.FIRE_119 -> {
                            BigAction(
                                "119 전화 연결",
                                onClick = onDial119,
                                container = HSafeColors.FireOrange
                            )
                            BigAction(
                                "119 소방 메시지 보내기",
                                onClick = onSend119Fire,
                                enabled = selected != AccidentKind.UNKNOWN,
                                container = HSafeColors.FireOrange
                            )
                        }"""
if old_119 not in text:
    raise SystemExit("119 CTA block not found")
text = text.replace(old_119, new_119)

# CompactField larger
old_cf = """@Composable
private fun CompactField(label: String, value: String, onChange: (String) -> Unit, enabled: Boolean) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().height(64.dp),
        enabled = enabled,
        singleLine = true
    )
}"""
new_cf = """@Composable
private fun CompactField(label: String, value: String, onChange: (String) -> Unit, enabled: Boolean) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, fontSize = 18.sp) },
        textStyle = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.fillMaxWidth().height(76.dp),
        enabled = enabled,
        singleLine = true
    )
}"""
if old_cf not in text:
    raise SystemExit("CompactField not found")
text = text.replace(old_cf, new_cf)

old_chip = """        label = { Text(label, fontWeight = FontWeight.SemiBold) },"""
new_chip = """        label = { Text(label, fontWeight = FontWeight.SemiBold, fontSize = 20.sp) },"""
text = text.replace(old_chip, new_chip, 1)

screens.write_text(text, encoding="utf-8")
print("Screens.kt updated")
