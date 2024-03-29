/*
 * A DCCTech © 2022 - 2023 All Rights Reserved. This copyright notice is the exclusive property of DCCTech and
 * is hereby granted to users for use of DCCTech's intellectual property. Any reproduction, modification, distribution,
 * or other use of DCCTech's intellectual property without prior written consent is strictly prohibited.
 *
 */
package io.dcctech.lan.spy.desktop.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.dcctech.lan.spy.desktop.common.R
import io.dcctech.lan.spy.desktop.data.Client
import io.dcctech.lan.spy.desktop.data.LogLevel
import io.dcctech.lan.spy.desktop.data.NetworkService
import io.dcctech.lan.spy.desktop.data.Status
import io.dcctech.lan.spy.desktop.window.LanSpyDesktopWindowState
import kotlinx.coroutines.*
import java.net.NetworkInterface
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.swing.JOptionPane


/**
Prints an error message to the standard error output stream.
@param errorMsg the error message to print
 */
fun printErr(errorMsg:String){
    System.err.println(errorMsg)
}

/**
Retrieves the application title from the given LanSpyDesktopWindowState object.
The title is composed of the application name and the current status of the discovery process.
@param state The LanSpyDesktopWindowState object that contains the current state of the application.
@return The composed application title as a String.
 */
fun getAppTitleFromState(state: LanSpyDesktopWindowState): String {
    val changeMark = state.process
    return "${R.appName} $changeMark"
}


/**
A variable that holds a DateTimeFormatter object used to format dates and times.
The pattern used is "yyyy-MM-dd HH:mm:ss" and the timezone is set to the system default.
 */
var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    .withZone(ZoneId.systemDefault())

/**
Logs a message with this log level to the console.
This function extends the LogLevel enum with a log function, which takes in a string parameter msg that represents
the message to be logged. The function checks the log level and prints the message to either the standard output stream
or the standard error output stream using the println(msg: String) or printErr(msg: String) -> System.err.println(msg: String) functions.
@param msg The message is to be logged.
 */
fun LogLevel.log(msg: String) {
    when{
        (this in listOf(LogLevel.CRITICAL, LogLevel.ERROR) ) -> printErr("$this: $msg")
        else -> println("$this: $msg")
    }
}

/**
A function that retrieves network information for the current device and adds the information to the result list of the application state.
It takes in a LanSpyDesktopWindowState object which holds the current state of the application.
The function retrieves network interfaces and their corresponding IP addresses, excluding local and loopback addresses.
For each valid IP address, it adds a new client or network service to the result list of the application state.
Note that this function does not return anything, it only updates the state of the application.
 */
suspend fun getNetworkInformation(state: LanSpyDesktopWindowState) {

    while (state.isRunning) {
        try {
            val interfaces = withContext(Dispatchers.IO) {
                NetworkInterface.getNetworkInterfaces()
            }

            for (intf in interfaces) {
                val addresses = intf.inetAddresses
                for (addr in addresses) {
                    if (!addr.isLinkLocalAddress && !addr.isLoopbackAddress) {
                        val client = Client(
                            status = Status.VISIBLE,
                            name = addr.hostName,
                            interfaceName = intf.displayName,
                            address = addr.hostAddress,
                            mac = addr.address.getMac(),
                            lastTime = Instant.now()
                        )

                        // All discovered clients have been set in the state variable
                        state.addDeviceToResult(client)

                        LogLevel.DEBUG.log(client.toString())
                    } else {
                        val networkService = NetworkService(
                            displayName = intf.displayName,
                            index = "${intf.index}",
                            hardwareAddress = intf.hardwareAddress.getMac(),
                            mtu = intf.mtu,
                            address = intf.inetAddresses().toString(),
                            lastTime = Instant.now(),
                            status = Status.VISIBLE,
                            name = intf.name,
                            mac = intf.hardwareAddress.getMac()

                        )
                        // All discovered network service have been set in the state variable
                        state.addNetwork(networkService)
                        LogLevel.DEBUG.log(networkService.toString())

                    }
                }
            }

            delay(state.application.settings.delayInDiscovery)

        } catch (t: Throwable) {
            LogLevel.ERROR.log("${t.localizedMessage}\n ${t.printStackTrace()}")
        }
    }

}

/**
 * Extract each array of mac address and convert it to hexadecimal with the following format: 08-00-27-DC-4A-9E.
 * */
fun ByteArray?.getMac(): String {
    if (this == null) return R.unknown

    var macAddress = ""

    for (i in this.indices) {
        macAddress += java.lang.String.format("%02X%s", this[i], if (i < this.size - 1) "-" else "")
    }

    return macAddress
}


fun showNotification(title: String, message: String) {
    JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE)
}

@Composable
fun title(title: String) = Text(textAlign = TextAlign.Center, text = title, modifier = Modifier.padding(5.dp))



/**
A function that takes a suspendable function and launches it in a new coroutine scope using the GlobalScope.
Exceptions inside the function are caught and logged in the standard error output stream.
@param func The suspendable function to be launched.
 */
@OptIn(DelicateCoroutinesApi::class)
fun io(func: suspend () -> Unit) = GlobalScope.launch {
    try {
        func()
    } catch (t: Throwable) {
        LogLevel.ERROR.log("${t.localizedMessage}\n ${t.printStackTrace()}")
    }
}