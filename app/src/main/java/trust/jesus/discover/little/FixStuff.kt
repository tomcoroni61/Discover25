package trust.jesus.discover.little

class FixStuff {
    interface Filenames {
        companion object {
            const val GlobalValues: String = "GlobalVals.txt"
            const val lineBreak: String = "~LB~"
            const val log: String = "fa_log.txt" //=internlog
            const val merkVers: String = "merkVers"
            const val jsonLsFName: String = "jsonLsFName.txt"
            const val wortListfile: String = "wortListfile.txt"
            const val LogMaxLines: Int = 1800
        }
    }
    interface Others {
        companion object
    }
}