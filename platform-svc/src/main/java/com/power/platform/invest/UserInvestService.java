package com.power.platform.invest;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.power.platform.cache.Cache;
import com.power.platform.common.exception.WinException;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 用户投资service
 * 
 * @author Mr.Jia
 * 
 */
@Component
@Path("/invest")
@Service("userInvestService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class UserInvestService {

	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;

	/**
	 * 用户投资接口
	 * 
	 * @param from
	 * @param token
	 * @param amount
	 * @param vouid
	 * @param projectId
	 * @return
	 */
	@POST
	@Path("/saveUserInvest")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> saveUserInvest(@FormParam("from") Integer from, @FormParam("token") String token, @FormParam("amount") Double amount, @FormParam("vouid") String vouid, @FormParam("projectId") String projectId, @FormParam("block") String block, @Context HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>();

		try {
			// 判断必要参数是否为空
			if (StringUtils.isBlank(projectId) || StringUtils.isBlank(token) || StringUtils.isBlank(block) || from == null || amount == null) {
				result.put("state", "2");
				throw new Exception("缺少参数必要参数");
			}
			if (amount <= 0) {
				result.put("state", "1");
				throw new Exception("系统错误");
			}

			String ip = (String) request.getAttribute("ip");
			ip = ip.replace("_", ".");

			// 从缓存获取用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			UserInfo user = principal.getUserInfo();
			UserAccountInfo account = principal.getUserAccountInfo();

			Map<String, Object> map = new HashMap<String, Object>();
			if (user == null || account == null) {
				throw new Exception("用户登录信息错误，请重新登录");
			} else {

				String lockNum = cache.get(account.getId());
				if (lockNum == null || lockNum.length() > 3) {
					cache.set(account.getId(), 120, "1");
					map = wloanTermInvestService.insertUserInvestInfo(token, projectId, amount, vouid, user, account, ip);
					account = (UserAccountInfo) map.get("userAccount");
					principal.setUserAccountInfo(account);
					cache.set(token, 1200, principal);
					map.remove("userAccount");
					cache.set(account.getId(), 120, "0");
				} else {
					Integer lock = Integer.parseInt(lockNum);
					if (lock > 0) {
						while (lock > 0) {
							Thread.sleep(3000);
							lockNum = cache.get(account.getId());
							if (lockNum == null) {
								break;
							}
							lock = Integer.parseInt(lockNum);
						}
						cache.set(account.getId(), 120, "1");
						map = wloanTermInvestService.insertUserInvestInfo(token, projectId, amount, vouid, user, account, ip);
						account = (UserAccountInfo) map.get("userAccount");
						principal.setUserAccountInfo(account);
						cache.set(token, 1200, principal);
						map.remove("userAccount");
						cache.set(account.getId(), 120, "0");
					}
				}
			}
			result.put("state", "0");
			result.put("message", "出借成功");
			result.put("data", map);
			return result;
		} catch (WinException e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			// result.put("data", null);
			result.put("state", "2");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			// result.put("data", null);
			result.put("state", "2");
			return result;
		}
	}

}
