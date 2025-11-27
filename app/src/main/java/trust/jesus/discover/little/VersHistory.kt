package trust.jesus.discover.little

import trust.jesus.discover.dlg_data.CsvData

class VersHistory {


    private val dataList: ArrayList<CsvData> = ArrayList()
    private var currentVers: Int = -1

    fun addVers(newVers: CsvData) {
        currentVers++
        dataList.add(newVers)
    }
    fun previousVers(): CsvData? {
        if (currentVers > 0) {
            currentVers--
            return dataList[currentVers]
        }
        return null
    }
    fun nextVers(): CsvData? {
        if (currentVers < dataList.size - 1) {
            currentVers++
            return dataList[currentVers]
        }
        return null
    }
    fun currentVers(): CsvData? {
        if (currentVers < dataList.size) {
            return dataList[currentVers]
        }
        return null
    }

}