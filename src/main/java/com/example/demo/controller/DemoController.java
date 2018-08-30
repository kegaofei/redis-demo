package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Article;

import redis.clients.jedis.Jedis;

@RestController
public class DemoController {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final int ONE_WEEK_IN_SECONDS = 7 * 86400;
    private static final int VOTE_SCORE = 432;
    
    @PostMapping(value = "/cancelArticleVote" )
    public void cancelArticleVote(Article article) {
    	Jedis conn = new Jedis("localhost");
    	cancelArticleVote(conn, article.getUser(), article.getId());
    }
    
    @PostMapping(value = "/articleVote" )
    public void articleVote(Article article) {
    	Jedis conn = new Jedis("localhost");
    	articleVote(conn, article.getUser(), article.getId());
    }

    @PostMapping(value = "/addArticle" )
	public void addArticle(Article article) {
		Jedis conn = new Jedis("localhost");
		postArticle(conn, article.getUser(), article.getTitle(), article.getLink());
	}
	
	@GetMapping(value = "getArticlesByScore")
	public List<Map<String,String>> getArticlesByScore(){
		Jedis conn = new Jedis("localhost");
		return getArticles(conn, "score");
	}
	
	@GetMapping(value = "getArticlesByTime")
	public List<Map<String,String>> getArticlesByTime(){
		Jedis conn = new Jedis("localhost");
		return getArticles(conn, "time");
	}
	
	public void cancelArticleVote(Jedis conn, String user, String article) {
		
		long cutoff = (System.currentTimeMillis() / 1000) - ONE_WEEK_IN_SECONDS;
        if (conn.zscore("time", article) < cutoff){
            return;
        }
        
        String articleId = article.substring(article.indexOf(':') + 1);
        if (conn.srem("voted:" + articleId, user) == 1) {
            conn.zincrby("score", -VOTE_SCORE, article);
            conn.hincrBy(article, "votes", -1);
        }
        
	}
	
	public void articleVote(Jedis conn, String user, String article) {
        long cutoff = (System.currentTimeMillis() / 1000) - ONE_WEEK_IN_SECONDS;
        if (conn.zscore("time", article) < cutoff){
            return;
        }

        String articleId = article.substring(article.indexOf(':') + 1);
        if (conn.sadd("voted:" + articleId, user) == 1) {
            conn.zincrby("score", VOTE_SCORE, article);
            conn.hincrBy(article, "votes", 1);
        }
    }
	
	public List<Map<String,String>> getArticles(Jedis conn, String order) {

        Set<String> ids = conn.zrevrange(order, 0, -1);
        List<Map<String,String>> articles = new ArrayList<Map<String,String>>();
        for (String id : ids){
            Map<String,String> articleData = conn.hgetAll(id);
            articleData.put("id", id);
            articles.add(articleData);
        }

        return articles;
    }
	
	/**
	 * article:[articleId] hash
	 * time zset
	 * score zset
	 * voted:[articleId] set
	 */
	public void postArticle(Jedis conn, String user, String title, String link) {
        String articleId = String.valueOf(conn.incr("articleId"));

        String voted = "voted:" + articleId;
        conn.sadd(voted, user);
        conn.expire(voted, ONE_WEEK_IN_SECONDS);

        long now = System.currentTimeMillis() / 1000;
        String article = "article:" + articleId;
        HashMap<String,String> articleData = new HashMap<String,String>();
        articleData.put("title", title);
        articleData.put("link", link);
        articleData.put("user", user);
        articleData.put("now", String.valueOf(now));
        articleData.put("votes", "1");
        conn.hmset(article, articleData);
        conn.zadd("score", now + VOTE_SCORE, article);
        conn.zadd("time", now, article);

    }
}
