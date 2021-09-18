// BookService.aidl
package com.liyaan.mynew;

import com.liyaan.mynew.BookInfo;

interface BookService {
    BookInfo getBookList();

    void addBookInOut(inout BookInfo book);
}
