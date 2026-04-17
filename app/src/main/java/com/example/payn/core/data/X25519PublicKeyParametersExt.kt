package com.example.payn.core.data

import org.bouncycastle.crypto.params.X25519PublicKeyParameters
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import java.io.StringWriter

fun X25519PublicKeyParameters.toPEMFormat(): String {
    val spki = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(this)
    val derEncoded = spki.encoded

    val stringWriter = StringWriter()
    PemWriter(stringWriter).use { pemWriter ->
        pemWriter.writeObject(PemObject("PUBLIC KEY", derEncoded))
    }

    return stringWriter.toString()
}