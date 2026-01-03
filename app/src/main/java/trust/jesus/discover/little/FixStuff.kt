package trust.jesus.discover.little

class FixStuff {
    interface Filenames {
        companion object {
            //const val GlobalValues: String = "GlobalVals.txt"
            const val lineBreak: String = "~LB~"
            const val merkVers: String = "merkVers"
            const val seekFileExtn = ".sFe"
            const val defaultLsFName: String = "currentList$seekFileExtn"
            const val logMaxLines: Int = 1800
            const val logName = "logFile.txt"
            const val crashLogName = "crashLog.txt"
        }
    }

}