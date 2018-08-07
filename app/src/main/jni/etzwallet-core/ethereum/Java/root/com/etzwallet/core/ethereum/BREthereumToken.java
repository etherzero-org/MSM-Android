/*
 * EthereumToken
 *
 * Created by Ed Gamble <ed@etzwallet.com> on 3/20/18.
 * Copyright (c) 2018 etzwallet LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.etzwallet.core.ethereum;


import com.etzwallet.core.BRCoreJniReference;

public class BREthereumToken extends BRCoreJniReference {

    protected BREthereumToken (long jniReferenceAddress) {
        super (jniReferenceAddress);
    }

    public native String getAddress ();
    public native String getSymbol ();
    public native String getName ();
    public native String getDescription ();
    public native int getDecimals ();
    public native String getColorLeft ();
    public native String getColorRight ();


//    {
//        // BRD first... so we can find it.
//        "0x558ec3152e2eb2174905cd19aea4e34a23de9ad6", // address
//                "BRD", // Symbol
//                "BRD Token", // Name
//                "", // Desc
//                18, //Decimals
//                "#ff5193", //color left
//                "#f9a43a", //color right
//                {TOKEN_BRD_DEFAULT_GAS_LIMIT},  //92000
//                {{{.u64 = {TOKEN_BRD_DEFAULT_GAS_PRICE_IN_WEI_UINT64, 0, 0, 0}}}}, //500000000  0.5 GWEI
//        1
//    },


    public long getIdentifier () {
        return jniReferenceAddress;
    }


    protected static native long jniGetTokenBRD ();
    protected static native long[] jniTokenAll ();

    @Override
    public int hashCode() {
        return getAddress().toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return "BREthereumToken{" + getSymbol() + "}";
    }
}
