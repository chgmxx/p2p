package com.power.platform.cgb.service;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.cache.Cache;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.exception.WinException;
import com.power.platform.common.utils.FileUploadUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.sys.dao.AnnexFileDao;
import com.power.platform.sys.entity.AnnexFile;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 用户授权
 * 
 * @author YHAGZALUN WO SJIAOSY
 *
 */
@Component
@Path("/authorization")
@Service("cGBAuthorizationService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CGBAuthorizationService {

	private static final Logger LOG = LoggerFactory.getLogger(CGBAuthorizationService.class);
	private static final String FILE_PATH = Global.getConfig("upload_iconfile_path");
	/**
	 * 测试环境网关地址.
	 */
	// private static final String HOST = Global.getConfigUb("UB_HOST");
	// 商户号
	private static final String MERCHANT_ID = Global.getConfig("merchantId");
	// 存管系统为你分配的，用来对AES私钥加密，同时生成签名
	private static final String MERCHANT_RSA_PUBLIC_KEY = Global.getConfig("merchantRsaPublicKey");
	// 商户自己的RSA私钥
	private static final String MERCHANT_RSA_PRIVATE_KEY = Global.getConfig("merchantRsaPrivateKey");
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private AnnexFileDao annexFileDao;

	/**
	 * 
	 * 方法: webMemberAuthorizationCreate <br>
	 * 描述: 借款端（PC端WEB）用户授权数据封装. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月26日 下午12:59:23
	 * 
	 * @param from
	 * @param userId
	 * @param grant
	 * @return
	 */
	@POST
	@Path("/webMemberAuthorizationCreate")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> webMemberAuthorizationCreate(@FormParam("from") String from, @FormParam("userId") String userId, @FormParam("grant") String grant) {

		Map<String, Object> result = new HashMap<String, Object>();

		try {
			// 判断必要参数
			if (StringUtils.isBlank(from) || StringUtils.isBlank(userId) || StringUtils.isBlank(grant)) {
				result.put("state", "2");
				result.put("message", "缺少参数必要参数 ...");
				return result;
			}

			/**
			 * 业务请求参数封装.
			 */
			Map<String, String> params = new HashMap<String, String>();
			// 网贷平台唯一的用户编码
			params.put("userId", userId);
			// 此处可传以上一个或多个值，传多个值 用“,”英文半角逗号分隔;
			// 用户授权列表：INVEST-免密投资、REPAY-免密还款、SHARE_PAYMENT-免密缴费;
			params.put("grantList", grant);
			// 前台通知地址URL.
			String returnUrlParam = "?backto=borrowingWebAuthorization&id=".concat(userId);
			params.put("returnUrl", ServerURLConfig.RETURN_URL_BORROWING_WEB_AUTHORIZATION.concat(returnUrlParam));
			// 异步通知地址.
			params.put("callbackUrl", ServerURLConfig.CALLBACK_URL_BORROWING_WEB_AUTHORIZATION);
			/**
			 * 公共请求参数封装.
			 */
			// 接口名称：每个接口提供不同的编码.
			params.put("service", "web.member.authorization.create");
			// 签名算法，固定值：RSA.
			params.put("method", "RSA");
			// 由存管银行分配给网贷平台的唯一的商户编码.
			params.put("merchantId", MERCHANT_ID);
			// 请求来源1:(PC)2:(MOBILE).
			params.put("source", "1");
			// PC端无需传入，移动端需传入两位的数字：
			// 第一位表示请求发起自APP还是WAP。（1表示APP，2表示WAP），第二位表示请求来自的操作系统类型。（1表示IOS，2表示Android）注：移动端请求如果传入空，系统将默认按照12处理，可能出现页面样式不兼容.
			// params.put("mobileType", "");
			// 请求时间格式："yyyy-MM-dd HH:mm:ss".
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String requestTime = sdf.format(new Date());
			params.put("requestTime", requestTime);
			// 本次请求的唯一标识，防止重复提交；可由网贷平台自主生成，也可以调用存管系统提供的JAR包中方法生成.
			params.put("reqSn", IdGen.uuid());
			// 服务版本号.
			params.put("version", "1.0.0");
			// 每次请求的业务参数的签名值，详细请参考【签名】.
			// "RSA"商户私钥加密签名.
			String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
			params.put("signature", sign);
			String paramsJsonStr = JSON.toJSONString(params);
			LOG.info("参数列表：" + paramsJsonStr);
			// 商户自己的RSA公钥加密.
			Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);

			String data = encryptRet.get("data");
			data = URLEncoder.encode(data, "UTF-8");
			String tm = encryptRet.get("tm");
			tm = URLEncoder.encode(tm, "UTF-8");
			encryptRet.put("tm", tm);
			encryptRet.put("data", data);
			encryptRet.put("merchantId", MERCHANT_ID);

			result.put("state", "0");
			result.put("message", "用户授权接口请求数据封装成功 ...");
			result.put("encryptRet", encryptRet);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常 ...");
			result.put("state", "3");
			return result;
		}
	}

	/**
	 * 用户授权 PC端web
	 * 
	 * @param from
	 * @param token
	 * @param grant
	 * @return
	 */
	@POST
	@Path("/userAuthorizationWeb")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> userAuthorizationWeb(@FormParam("from") String from, @FormParam("token") String token, @FormParam("grant") String grant) {

		Map<String, Object> result = new HashMap<String, Object>();

		try {
			// 判断必要参数是否为空
			if (StringUtils.isBlank(from) || StringUtils.isBlank(token) || StringUtils.isBlank(grant)) {
				result.put("state", "2");
				throw new Exception("缺少参数必要参数");
			}

			// 从缓存获取用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			UserInfo user = principal.getUserInfo();

			Map<?, ?> map = new HashMap<String, Object>();
			if (user == null) {
				throw new Exception("用户登录信息错误，请重新登录");
			} else {
				map = userInfoService.userAuthorizationWeb(user, grant);
			}
			result.put("state", "0");
			result.put("message", "用户授权");
			result.put("data", map);
			return result;
		} catch (WinException e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("state", "3");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			return result;
		}
	}

	/**
	 * 用户授权 手机端H5-- 投资
	 * 
	 * @param from
	 * @param token
	 * @param grant
	 * @return
	 */
	@POST
	@Path("/userAuthorizationH5")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> userAuthorizationH5(@FormParam("from") String from, @FormParam("token") String token, @FormParam("grant") String grant) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			// 判断必要参数是否为空
			if (StringUtils.isBlank(from) || StringUtils.isBlank(token) || StringUtils.isBlank(grant)) {
				result.put("state", "2");
				throw new Exception("缺少参数必要参数");
			}

			// 从缓存获取用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			UserInfo user = principal.getUserInfo();
			Map<?, ?> map = new HashMap<String, Object>();
			if (user == null) {
				throw new Exception("用户登录信息错误，请重新登录");
			} else {
				map = userInfoService.userAuthorizationH5(user.getId(), grant, from);
			}
			result.put("state", "0");
			result.put("message", "用户授权");
			result.put("data", map);
			return result;
		} catch (WinException e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("state", "3");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			return result;
		}
	}

	/**
	 * 用户授权 手机端H5-- 借款
	 * 
	 * @param from
	 * @param token
	 * @param grant
	 * @return
	 */
	@POST
	@Path("/credituserAuthorizationH5")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> credituserAuthorizationH5(@FormParam("from") String from, @FormParam("token") String token, @FormParam("grant") String grant) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			// 判断必要参数是否为空
			if (StringUtils.isBlank(from) || StringUtils.isBlank(token) || StringUtils.isBlank(grant)) {
				result.put("state", "2");
				throw new Exception("缺少参数必要参数");
			}

			// 从缓存获取用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			CreditUserInfo user = principal.getCreditUserInfo();
			Map<?, ?> map = new HashMap<String, Object>();
			if (user == null) {
				throw new Exception("用户登录信息错误，请重新登录");
			} else {
				map = userInfoService.userAuthorizationH5(user.getId(), grant, from);
			}
			result.put("state", "0");
			result.put("message", "用户授权");
			result.put("data", map);
			return result;
		} catch (WinException e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("state", "3");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			return result;
		}
	}

	/**
	 * 投资端上传头像
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/uploadAvatar")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> uploadAvatar(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();

		// 普通文本表单字段.
		int isTextFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";
		// Z资料ID
		// String id = IdGen.uuid();
		String path = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		LOG.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			LOG.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			LOG.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 资料信息上传.
		 */
		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					LOG.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token")) {
						LOG.info("FORM DATA:{投资端上传头像参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 客户ID.
					String jedisUserId = JedisUtils.get(map.get("token"));

					if (null != jedisUserId) {
						UserInfo user = userInfoService.getCgb(jedisUserId);
						if (null != user) {

							// 先判断是否已经上传了头像
							AnnexFile annexFileAvaTar = new AnnexFile();
							annexFileAvaTar.setOtherId(user.getId());
							annexFileAvaTar = annexFileDao.findByOtherId(user.getId());
							if (annexFileAvaTar != null) {
								// 表数据删除.
								annexFileDao.deleteAnnexFile(annexFileAvaTar.getId());
								// 文件删除.
								File file = new File(FILE_PATH + File.separator + annexFileAvaTar.getUrl());
								if (file.delete()) {
									LOG.info("File delete success.");
								} else {
									LOG.info("File delete failure.");
								}
							}

							// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
							String name = item.getName();
							// 只获取文件名.
							fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
							// 文件扩展名
							fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
							LOG.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
							// 文件上传路径.
							path = FileUploadUtils.createFilePath(fileFormat);
							LOG.info("PATH:" + path);

							// 新的文件名.
							newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
							boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());

							if (flag) {
								LOG.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
								/**
								 * 保存用户头像信息附件.
								 */
								AnnexFile annexFile = new AnnexFile();
								annexFile.setId(IdGen.uuid());
								annexFile.setOtherId(user.getId());
								annexFile.setUrl(path);
								annexFile.setCreateDate(new Date());
								annexFile.setUpdateDate(new Date());
								annexFile.setType("201");//
								annexFile.setRemarks("用户头像");
								int tag = annexFileDao.insert(annexFile);
								if (tag > 0) {
									LOG.info("用户上传头像保存成功");
								}
							}
						}
					} else {
						LOG.info("FORM DATA:{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						return result;
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "5");
			result.put("message", "系统异常.");
			LOG.error("fn:uploadAvatar,{" + e.getMessage() + "}");
			return result;
		}
		result.put("state", "0");
		result.put("message", "用户上传头像上传成功.");
		return result;
	}
}
