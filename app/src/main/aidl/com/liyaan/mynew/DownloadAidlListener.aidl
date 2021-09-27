// DownloadAidlListener.aidl
package com.liyaan.mynew;

// Declare any non-default types here with import statements

interface DownloadAidlListener {
    void onProgress(int progress);

        void onSuccess();

        void onFail();

        void onPaused();

        void onCanceled();
}
