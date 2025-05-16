package com.bca.food_ordering_app.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable


data class OrderDetails(
    var userId: String? = null,
    var userName: String? = null,
    var foodName: MutableList<String>? = null,
    var foodPrice: MutableList<String>? = null,
    var foodQuantities: MutableList<Int>? = null,
    var address: String? = null,
    var phoneNumber: String? = null,
    var totalAmount: String? = null,
    var currentTime: Long = 0,
    var itemPushKey: String? = null,
    var orderAccepted: Boolean = false,
    var paymentReceived: Boolean = false
) : Serializable {

    constructor(parcel: Parcel) : this(
        userId = parcel.readString(),
        userName = parcel.readString(),
        foodName = parcel.createStringArrayList()?.toMutableList(),
        foodPrice = parcel.createStringArrayList()?.toMutableList(),
        foodQuantities = parcel.createIntArray()?.toMutableList(),
        address = parcel.readString(),
        phoneNumber = parcel.readString(),
        totalAmount = parcel.readString(),
        currentTime = parcel.readLong(),
        itemPushKey = parcel.readString(),
        orderAccepted = parcel.readByte() != 0.toByte(),
        paymentReceived = parcel.readByte() != 0.toByte()
    )

    constructor(
        userId: String?,
        userName: String?,
        foodName: ArrayList<String>?,
        foodPrice: ArrayList<String>?,
        foodQuantities: ArrayList<Int>?,
        address: String?,
        phoneNumber: String?,
        totalAmount: String?,
        currentTime: Long,
        itemPushKey: String?,
        orderAccepted: Boolean,
        paymentReceived: Boolean
    ) : this()

  fun writeToParcel(parcel: Parcel, flags: Int) {

    }

     fun describeContents(): Int = 0



    companion object CREATOR : Parcelable.Creator<OrderDetails> {
        override fun createFromParcel(parcel: Parcel): OrderDetails {
            return OrderDetails(parcel)
        }

        override fun newArray(size: Int): Array<OrderDetails?> {
            return arrayOfNulls(size)
        }
    }
}


fun Parcel.writeIntList(list: List<Int>?) {
    if (list == null) {
        writeInt(-1)
        return
    }
    writeInt(list.size)
    list.forEach { writeInt(it) }
}

fun Parcel.readIntList(): MutableList<Int>? {
    val size = readInt()
    if (size == -1) return null
    val list = mutableListOf<Int>()
    repeat(size) { list.add(readInt()) }
    return list
}
