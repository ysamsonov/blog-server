package me.academeg.blog.dal.utils.helperentities;

import lombok.Getter;
import lombok.Setter;

import static me.academeg.blog.dal.utils.Relations.setManyToOne;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @date 23.02.2017
 */
@Setter
@Getter
public class Page {
    private int number;

    private Book book;

    public Page(int number) {
        this.number = number;
    }

    public Page setBook(Book book) {
        return setManyToOne(
            this,
            book,
            Page::getBook,
            book1 -> this.book = book,
            Book::addPage,
            Book::removePage
        );
    }
}
