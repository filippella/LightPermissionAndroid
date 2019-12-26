package com.filippoengidashet.permission

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.IntDef

import com.filippoengidashet.permission.PermissionResult.State.Type.DENIED_SHOW_RATIONALE
import com.filippoengidashet.permission.PermissionResult.State.Type.GRANTED
import com.filippoengidashet.permission.PermissionResult.State.Type.PERMANENTLY_DENIED
import com.filippoengidashet.permission.PermissionResult.State.Type.REVOKED_BY_POLICY

/**
 * @author Filippo Engidashet
 * @version 1.0.0
 * @since Sun, 2019-12-15 at 21:11.
 */
class PermissionResult(val permission: String?, @State val state: Int) : Parcelable {

    private constructor(parcel: Parcel) : this(parcel.readString(), parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(permission)
        parcel.writeInt(state)
    }

    override fun describeContents() = 0

    @Retention(AnnotationRetention.RUNTIME)
    @IntDef(GRANTED, DENIED_SHOW_RATIONALE, PERMANENTLY_DENIED, REVOKED_BY_POLICY)
    annotation class State {

        companion object Type {

            const val GRANTED = 0
            const val DENIED_SHOW_RATIONALE = 1
            const val PERMANENTLY_DENIED = 2
            const val REVOKED_BY_POLICY = 3

            fun from(@State state: Int): String {
                return when (state) {
                    GRANTED -> "GRANTED"
                    DENIED_SHOW_RATIONALE -> "DENIED_SHOW_RATIONALE"
                    PERMANENTLY_DENIED -> "PERMANENTLY_DENIED"
                    REVOKED_BY_POLICY -> "REVOKED_BY_POLICY"
                    else -> throw IllegalArgumentException("Invalid state.")
                }
            }
        }
    }

    companion object CREATOR : Parcelable.Creator<PermissionResult> {

        override fun createFromParcel(parcel: Parcel): PermissionResult {
            return PermissionResult(parcel)
        }

        override fun newArray(size: Int): Array<PermissionResult?> {
            return arrayOfNulls<PermissionResult?>(size)
        }
    }
}
