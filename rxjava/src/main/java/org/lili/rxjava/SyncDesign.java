package org.lili.rxjava;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lili
 * @date 2021/7/6 1:06
 */
public class SyncDesign {
    List<Integer> searchForArticles(String query) {
        return null;
    }
    PersistentArticle loadArticle(Integer articleId) {
        return null;
    }
    Integer fetchLikeCount(Integer articleId) {
        return null;
    }

    public List<Article> getArticle() {
        List<Integer> searchResult = searchForArticles("dhaka");//search server
        List<Article> result = new ArrayList<>();
        for(Integer id : searchResult) {
            PersistentArticle pa = loadArticle(id);//db server
            Integer likes = fetchLikeCount(id);//network
            result.add(new Article(pa, likes));
        }
        return result;
    }
}
