package com.cobee.bookstore.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cobee.bookstore.entity.Tbook;
import com.cobee.bookstore.service.AbstractService;
import com.cobee.bookstore.service.ITbookService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TbookServiceRedisImpl extends AbstractService implements ITbookService { 
	
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	
	public Tbook get(String isbn) {
		if (StringUtils.isBlank(isbn))
		{
			return null;
		}
		Tbook tbook = null;
		try
		{
			String bookJsonStr = stringRedisTemplate.opsForHash().get("hash:books", isbn).toString();
			if (StringUtils.isNotBlank(bookJsonStr))
			{
				ObjectMapper objectMapper = new ObjectMapper();
				tbook = objectMapper.readValue(bookJsonStr, Tbook.class);
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tbook;
	}

	public List<Tbook> listAll() {
		
		List<Tbook> bookList = null;
		
		Set<String> booksSet = stringRedisTemplate.opsForZSet().reverseRange("zset:books", 0, -1);
		if (!CollectionUtils.isEmpty(booksSet))
		{
			ObjectMapper objectMapper = new ObjectMapper();
			bookList = new ArrayList<Tbook>();
			for (String str : booksSet)
			{
				String bookJsonStr = stringRedisTemplate.opsForHash().get("hash:books", str).toString();
				if (StringUtils.isNotBlank(bookJsonStr))
				{
					try {
						Tbook tbook = objectMapper.readValue(bookJsonStr, Tbook.class);
						// 查找书本被浏览的次数
						Double pageViews = stringRedisTemplate.opsForZSet().score("zset:bookpageview", tbook.getIsbn());
						if (pageViews != null)
						{
							tbook.setPageViews(pageViews.intValue());
						}
						else
						{
							tbook.setPageViews(0);
						}
						bookList.add(tbook);
					} catch (JsonParseException e) {
						e.printStackTrace();
					} catch (JsonMappingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
		else
		{
			bookList = Collections.emptyList();
		}
		
		return bookList;
	}

	@Override
	public void addBookPageView(String isbn) {
		// 如果key不存在，会自动创建
		stringRedisTemplate.opsForZSet().incrementScore("zset:bookpageview", isbn, 1.0D);
	}

	@Override
	public Integer getBookPageView(String isbn) {
		// 如果key不存在，会自动创建
		Double pageViews = stringRedisTemplate.opsForZSet().score("zset:bookpageview", isbn);
		return pageViews == null ? 0 : pageViews.intValue();
	}

}
