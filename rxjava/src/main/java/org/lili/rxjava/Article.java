package org.lili.rxjava;

/**
 * @author lili
 * @date 2021/7/6 1:07
 */
public class Article {
    private PersistentArticle pa;
    private int likes;

    public Article(PersistentArticle pa, Integer likes) {
        this.pa = pa;
        this.likes = likes;
    }
}
