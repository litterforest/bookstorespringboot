package com.cobee.bookstore.web;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import com.cobee.bookstore.component.AuctionCallable;

@Controller
@RequestMapping("/Auction")
public class AuctionController extends AbstractController {

	@Resource
	private StringRedisTemplate stringRedisTemplate;
	
	@GetMapping(value = "/acutionPage")
	public String acutionPage()
	{
		return "auction";
	}
	
	@PostMapping(value = "/doAuction", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Callable<Map<String, Object>> doAuction(final String userno, String isbn)
	{
		// 1,增加到图书抢购队列
		stringRedisTemplate.opsForList().rightPush("auction:book:queue:" + isbn, userno);
		return new AuctionCallable(userno, isbn);
	}
	
	@PostMapping(value = "/doAuction1", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public DeferredResult<Map<String, Object>> doAuction1(final String userno, String isbn) {
		
	    DeferredResult<Map<String, Object>> deferredResult = new DeferredResult<Map<String, Object>>();
	    
	    
	    
	    return deferredResult;
	}
	
}
