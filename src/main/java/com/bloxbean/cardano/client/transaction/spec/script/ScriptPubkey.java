package com.bloxbean.cardano.client.transaction.spec.script;

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.UnsignedInteger;
import com.bloxbean.cardano.client.crypto.KeyGenUtil;
import com.bloxbean.cardano.client.crypto.Keys;
import com.bloxbean.cardano.client.crypto.VerificationKey;
import com.bloxbean.cardano.client.exception.CborDeserializationException;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.bloxbean.cardano.client.util.HexUtil;
import com.bloxbean.cardano.client.util.Tuple;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class ScriptPubkey implements NativeScript {
    private final static Logger LOG = LoggerFactory.getLogger(ScriptPubkey.class);

    private String keyHash;
    private ScriptType type;

    public ScriptPubkey() {
        this.type = ScriptType.sig;
    }

    public ScriptPubkey(String keyHash) {
        this();
        this.keyHash = keyHash;
    }

    public byte[] toBytes() {
        if (keyHash == null || keyHash.length() == 0)
            return new byte[0];

        byte[] keyHashBytes = new byte[0];
        try {
            keyHashBytes = HexUtil.decodeHexString(keyHash);
        } catch (Exception e) {
            LOG.error("Error ", e);
        }
        return keyHashBytes;
    }

    public DataItem serializeAsDataItem() {
        Array array = new Array();
        array.add(new UnsignedInteger(0));
        array.add(new ByteString(HexUtil.decodeHexString(keyHash)));
        return array;
    }

    public static ScriptPubkey deserialize(Array array) throws CborDeserializationException {
        ScriptPubkey scriptPubkey = new ScriptPubkey();
        ByteString keyHashBS = (ByteString)(array.getDataItems().get(1));
        scriptPubkey.setKeyHash(HexUtil.encodeHexString(keyHashBS.getBytes()));
        return scriptPubkey;
    }

    public static ScriptPubkey create(VerificationKey vkey) {
        return new ScriptPubkey(KeyGenUtil.getKeyHash(vkey));
    }

    public static Tuple<ScriptPubkey, Keys> createWithNewKey() throws CborSerializationException {
        Keys keys = KeyGenUtil.generateKey();

        ScriptPubkey scriptPubkey = ScriptPubkey.create(keys.getVkey());
        return new Tuple<ScriptPubkey, Keys>(scriptPubkey, keys);
    }
}
