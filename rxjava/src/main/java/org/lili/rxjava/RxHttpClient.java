package org.lili.rxjava;


import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @author lili
 * @date 2021/7/4 23:59
 */
public class RxHttpClient {

    public static Maybe<String> httpGet(String url, int timeout) {
        return Maybe.create(new MaybeOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull MaybeEmitter<String> emitter) throws Exception {
                emitter.onSuccess(url);
            }
        }).map(new Function<String, String>() {
            @Override
            public String apply(@NonNull String s) throws Exception {
                CloseableHttpClient client = HttpClients.createDefault();
                HttpGet get = new HttpGet(s);
                CloseableHttpResponse response = client.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("StatusCode:" + statusCode);
                HttpEntity entity = response.getEntity();
                String respStr = null;
                if (entity != null) {
                    respStr = EntityUtils.toString(entity, "UTF-8");
                }
                System.out.println(respStr);
                EntityUtils.consume(entity);
                return respStr;
            }
        });
    }


    public void get() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Exception {
                String url = "http://www.163.com";
                emitter.onNext(url);
            }
        }).map(new Function<String, CloseableHttpResponse>() {
            @Override
            public CloseableHttpResponse apply(@NonNull String url) throws Exception {
                CloseableHttpClient client = HttpClients.createDefault();
                HttpGet get = new HttpGet(url);
                return client.execute(get);
            }
        }).subscribe(new Consumer<CloseableHttpResponse>() {
            @Override
            public void accept(CloseableHttpResponse response) throws Exception {
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("StatusCode:" + statusCode);
                HttpEntity entity = response.getEntity();
                String respStr = null;
                if (entity != null) {
                    respStr = EntityUtils.toString(entity, "UTF-8");
                }
                System.out.println(respStr);
                EntityUtils.consume(entity);
            }
        });
    }

    public static void main(String[] args) {
//        RxHttpClient h = new RxHttpClient();
//        h.get();

        RxHttpClient.httpGet("http://www.163.com", 6000)
                .subscribe(s -> System.out.println(s));
    }
}
