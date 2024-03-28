package hoods.com.jetshopping.data.room.converters

import androidx.room.TypeConverter
import java.util.Date

open class DateConverters {

    @TypeConverter
    fun fromDate(date:Long?): Date?{
        return date?.let { Date(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?):Long?{
        return date?.time
    }

}