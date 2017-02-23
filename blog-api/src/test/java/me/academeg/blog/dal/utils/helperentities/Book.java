package me.academeg.blog.dal.utils.helperentities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static me.academeg.blog.dal.utils.Relations.*;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @date 23.02.2017
 */
public class Book {

    @Getter
    @Setter
    private String name;

    private List<Page> pages = new ArrayList<>();

    public Book(String name) {
        this.name = name;
    }

    public Book addPage(Page page) {
        return addOneToMany(
            this,
            this.pages,
            page,
            Page::getBook,
            Page::setBook
        );
    }

    public Book removePage(Page page) {
        return removeOneToMany(
            this,
            this.pages,
            page,
            Page::setBook
        );
    }

    public Collection<Page> getPages() {
        return getOneToMany(pages);
    }

    public void setPages(Collection<Page> pages) {
        setOneToMany(
            this,
            this.pages,
            pages,
            Page::getBook,
            Page::setBook
        );
    }
}
