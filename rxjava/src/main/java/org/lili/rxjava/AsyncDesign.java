package org.lili.rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static io.reactivex.rxjava3.core.Observable.zip;

/**
 * @author lili
 * @date 2021/7/6 1:11
 */
public class AsyncDesign {
    Observable<Integer> searchForArticles(String query) {
        return null;
    }

    Observable<PersistentArticle> loadArticle(Integer articleId) {
        return null;
    }

    Observable<Integer> fetchLikeCount(Integer articleId) {
        return null;
    }



    public Observable<Article> getArticle(String query) {
        Observable<Integer> ids = searchForArticles(query);
        return ids.flatMap(id -> {
            Observable<PersistentArticle> arts = loadArticle(id);
            Observable<Integer> likes = fetchLikeCount(id);
            return arts.flatMap(art -> {
                return likes.flatMap(like -> {
                    return Observable.just(new Article(art, like));
                });
            });
        });
    }

    public Observable<Article> getArticleWithZip(String query) {
        Observable<Integer> ids = searchForArticles(query);
        return ids.flatMap(id -> {
            Observable<PersistentArticle> arts = loadArticle(id);
            Observable<Integer> likes = fetchLikeCount(id);
            return zip(arts, likes, (art, lc) -> {
                return new Article(art, lc);
            });
        });
    }


    public Observable<Article> getArticleWithZipAndJava8(String query) {
        return searchForArticles(query).flatMap(id -> {
            return zip(loadArticle(id),
                    fetchLikeCount(id),
                    Article::new);
        });
    }


    public Observable<Article> getArticleWithSchedulerIo(String query) {
        return searchForArticles(query).flatMap(id -> {
            return Observable.just(id)
                    .subscribeOn(Schedulers.io())
                    .flatMap(i -> {
                        return zip(loadArticle(i),
                                fetchLikeCount(i),
                                Article::new);
                    });
        });
    }


}
