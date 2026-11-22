package com.desktop.environment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF10141D)
private val Panel = Color(0xFF1B2331)
private val Accent = Color(0xFF62A8FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DesktopApp(
            onPickFile = { launcher ->
                launcher.launch(arrayOf("*/*"))
            },
            onOpenUrl = { url ->
                runCatching { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
            }
        ) }
    }
}

@Composable
fun DesktopApp(
    onPickFile: (androidx.activity.result.ActivityResultLauncher<Array<String>>) -> Unit,
    onOpenUrl: (String) -> Unit
) {
    var page by remember { mutableStateOf("Desktop") }
    var selectedFile by remember { mutableStateOf("No file selected") }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) selectedFile = uri.lastPathSegment ?: uri.toString()
    }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Accent, background = Bg, surface = Panel
    )) {
        Box(Modifier.fillMaxSize().background(Bg)) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth().background(Color(0xFF202B3B)).padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Window, null, tint = Accent)
                    Spacer(Modifier.width(10.dp))
                    Text("Desktop", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.weight(1f))
                    Text("LOCAL + CLOUD", color = Color(0xFF9FB5D1), fontSize = 11.sp)
                }
                Row(Modifier.weight(1f)) {
                    Column(
                        Modifier.width(68.dp).fillMaxHeight().background(Color(0xFF151D29)).padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        SideIcon(Icons.Default.Home, "Desktop") { page = "Desktop" }
                        SideIcon(Icons.Default.Folder, "Files") { page = "Files" }
                        SideIcon(Icons.Default.Terminal, "Runtime") { page = "Runtime" }
                        SideIcon(Icons.Default.Cloud, "Cloud") { page = "Cloud" }
                        SideIcon(Icons.Default.Settings, "Settings") { page = "Settings" }
                    }
                    Column(Modifier.weight(1f).fillMaxHeight().padding(18.dp)) {
                        when (page) {
                            "Desktop" -> DesktopHome(
                                onFiles = { page = "Files" },
                                onRuntime = { page = "Runtime" },
                                onCloud = { page = "Cloud" }
                            )
                            "Files" -> FilesPage(selectedFile) { picker.launch(arrayOf("*/*")) }
                            "Runtime" -> RuntimePage()
                            "Cloud" -> CloudPage(onOpenUrl)
                            else -> SettingsPage()
                        }
                    }
                }
                Row(
                    Modifier.fillMaxWidth().background(Color(0xFF202B3B)).padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Widgets, null, tint = Accent)
                    Spacer(Modifier.width(10.dp))
                    Text("Start", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    Text("Desktop Android • Build 0.1.0", color = Color.LightGray, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun SideIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, click: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { click() }) {
        Icon(icon, contentDescription = label, tint = Accent, modifier = Modifier.size(25.dp))
        Text(label, color = Color.LightGray, fontSize = 9.sp)
    }
}

@Composable
private fun DesktopHome(onFiles: () -> Unit, onRuntime: () -> Unit, onCloud: () -> Unit) {
    Text("Welcome to Desktop", fontSize = 25.sp, fontWeight = FontWeight.Bold)
    Text("A hybrid Android workspace for local tools and remote Windows sessions.", color = Color(0xFFB5C2D3))
    Spacer(Modifier.height(22.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        HomeTile("File Manager", "Browse scripts and documents", Icons.Default.Folder, Modifier.weight(1f), onFiles)
        HomeTile("Runtime Center", "Local execution engines", Icons.Default.Terminal, Modifier.weight(1f), onRuntime)
    }
    Spacer(Modifier.height(10.dp))
    HomeTile("Cloud Desktop", "Connect to your Windows machine", Icons.Default.Cloud, Modifier.fillMaxWidth(), onCloud)
    Spacer(Modifier.height(20.dp))
    Text("Supported file families", fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    Text("EXE • BAT • CMD • PY • JS • PS1 • ZIP • RAR", color = Color(0xFFB5C2D3))
    Spacer(Modifier.height(12.dp))
    Text("Execution depends on the installed runtime or a connected cloud Windows host. Android cannot natively run Windows EXE files.", color = Color(0xFFFFD28A), fontSize = 12.sp)
}

@Composable
private fun HomeTile(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, click: () -> Unit) {
    Card(modifier.clickable { click() }, colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = Accent, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color(0xFFB5C2D3), fontSize = 11.sp)
        }
    }
}

@Composable
private fun FilesPage(selectedFile: String, choose: () -> Unit) {
    Text("File Manager", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(12.dp))
    Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
        Column(Modifier.padding(16.dp)) {
            Text("Selected file", color = Color.LightGray)
            Text(selectedFile, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            Button(onClick = choose) { Icon(Icons.Default.FolderOpen, null); Spacer(Modifier.width(8.dp)); Text("Choose file") }
        }
    }
    Spacer(Modifier.height(12.dp))
    Text("Choose a file to inspect it. Running a file requires a compatible local runtime or cloud Windows session.", color = Color(0xFFB5C2D3))
}

@Composable
private fun RuntimePage() {
    Text("Runtime Center", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(12.dp))
    val engines = listOf(
        "Python" to "Planned local runtime integration",
        "Node.js" to "Planned local runtime integration",
        "Linux / proot" to "Requires a supported userspace environment",
        "Wine + Box64" to "Experimental compatibility layer; device-dependent",
        "Windows EXE" to "Use cloud Windows for broad compatibility"
    )
    engines.forEach { (name, detail) ->
        Card(Modifier.fillMaxWidth().padding(bottom = 8.dp), colors = CardDefaults.cardColors(containerColor = Panel)) {
            Column(Modifier.padding(14.dp)) {
                Text(name, fontWeight = FontWeight.Bold)
                Text(detail, color = Color(0xFFB5C2D3), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun CloudPage(openUrl: (String) -> Unit) {
    Text("Cloud Desktop", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    Text("Connect to a Windows host using an RDP client or a browser-based remote desktop gateway.", color = Color(0xFFB5C2D3))
    Spacer(Modifier.height(14.dp))
    Button(onClick = { openUrl("https://remotedesktop.google.com/access/") }) {
        Icon(Icons.Default.OpenInBrowser, null); Spacer(Modifier.width(8.dp)); Text("Open remote desktop portal")
    }
    Spacer(Modifier.height(12.dp))
    Text("A cloud Windows computer or remote desktop gateway is not included. You will need to provide a host/account and configure secure access.", color = Color(0xFFFFD28A), fontSize = 12.sp)
}

@Composable
private fun SettingsPage() {
    Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(12.dp))
    Text("Project: Desktop")
    Text("Mode: Local + Cloud")
    Text("Interface: Windows-inspired desktop shell")
    Text("Target: Android 8.0+")
    Text("Device-friendly profile: low-memory mode planned")
}
