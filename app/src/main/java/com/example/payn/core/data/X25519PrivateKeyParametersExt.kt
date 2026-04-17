package com.example.payn.core.data

import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import java.io.StringWriter

fun X25519PrivateKeyParameters.toPEMFormat(): String {
    val privateKeyInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(this)
    val derEncoded = privateKeyInfo.encoded

    val stringWriter = StringWriter()
    PemWriter(stringWriter).use { pemWriter ->
        pemWriter.writeObject(PemObject("PRIVATE KEY", derEncoded))
    }

    return stringWriter.toString()
}