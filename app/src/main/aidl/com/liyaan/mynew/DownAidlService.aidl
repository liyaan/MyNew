// DownAidlService.aidl
package com.liyaan.mynew;

import com.liyaan.mynew.DownloadAidlListener;
// Declare any non-default types here with import statements

interface DownAidlService {
    void setListener(in DownloadAidlListener lis);
    void startDownload(String url);
    void pauseDownload();
    void cancelDownload();
}
