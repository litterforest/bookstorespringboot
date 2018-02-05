package com.cobee.bookstore.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cobee.bookstore.entity.Comment;
import com.cobee.bookstore.service.AbstractService;
import com.cobee.bookstore.service.ICommentService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("ICommentServiceImpl")
public class ICommentServiceImpl extends AbstractService implements ICommentService {
	
	@Resource
	private RedisTemplate<String, Object> redisTemplate;
	
	@Override
	public void add(Comment comment) {
		if (comment == null)
			return;
		try {
			comment.setCommentID(UUID.randomUUID().toString());
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonStr = objectMapper.writeValueAsString(comment);
			redisTemplate.opsForList().leftPush("list:comment:" + comment.getIsbn(), jsonStr);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Comment> listAll(String isbn) {
		if (StringUtils.isBlank(isbn))
		{
			return Collections.emptyList();
		}
		
		return list(isbn, 0, -1);
	}

	@Override
	public List<Comment> list(String isbn, Integer start, Integer end) {
		if (StringUtils.isBlank(isbn))
		{
			return Collections.emptyList();
		}
		if (start < 0)
		{
			return Collections.emptyList();
		}
		List<Comment> commentList = null;
		
		List<Object> list = redisTemplate.opsForList().range("list:comment:" + isbn, start, end);
		if (!CollectionUtils.isEmpty(list))
		{
			commentList = new ArrayList<Comment>();
			ObjectMapper objectMapper = new ObjectMapper();
			for(Object jsonStr : list)
			{
				try {
					Comment comment = objectMapper.readValue(jsonStr.toString(), Comment.class);
					// 加载评论的点赞数
					Double dianzhangcount = redisTemplate.opsForZSet().score("zset:comment:dianzhang", comment.getCommentID());
					comment.setThumbsupCount(dianzhangcount == null ? 0 : dianzhangcount.intValue());
					commentList.add(comment);
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}
		else
		{
			commentList = Collections.emptyList();
		}
			
		
		return commentList;
	}

	/**
	 * 返回评论最新的点赞数量
	 */
	@Override
	public Integer dianzhang(String commentID) {
		Integer count = 0;
		String redisKey = "zset:comment:dianzhang";
		
		redisTemplate.opsForZSet().incrementScore(redisKey, commentID, 1.0D);
		Double dianzhang = redisTemplate.opsForZSet().score(redisKey, commentID);
		if (dianzhang != null)
		{
			count = dianzhang.intValue();
		}
		
		return count;
	}

}
