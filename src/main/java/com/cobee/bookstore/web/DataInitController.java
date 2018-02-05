package com.cobee.bookstore.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cobee.bookstore.entity.Tbook;
import com.cobee.bookstore.service.ITbookService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("DataInit")
public class DataInitController extends AbstractController {

	@Resource(name = "tbookServiceImpl")
	private ITbookService ITbookService;
	@Resource
	private RedisTemplate<String, Object> redisTemplate;
	
	@GetMapping(value = "buildRedisData")
	public String buildRedisData(Model model)
	{
		String msg = "";
		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			List<Tbook> list = ITbookService.listAll();
			if (!CollectionUtils.isEmpty(list))
			{
				redisTemplate.delete("zset:books");
				redisTemplate.delete("hash:books");
				for (Tbook po : list)
				{
					String jsonStr = objectMapper.writeValueAsString(po);
					redisTemplate.opsForZSet().add("zset:books", po.getIsbn(), po.getPdate().getTime());
					redisTemplate.opsForHash().put("hash:books", po.getIsbn(), jsonStr);
				}
			}
			msg = "Redis数据创建成功";
		}
		catch(Exception e)
		{
			msg = "Redis数据创建失败";
			e.printStackTrace();
		}
		model.addAttribute("msg", msg);
		return "buildredis";
	}
	
}
