package com.xiaopeng.waterarmy.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 功能描述：
 * 
 *
 * @author iason
 * @version 1.0.0
 * @since 1.0.0 create on: 2017/3/13 9:53
 */
public class CollectionUtil {
	// .....................................................................................................
	/** 将String按指定的分隔符split，返回一个大小可变动的List<String> */
	private static List<String> string2List(String s, String separatorChars) {
		if (StringUtils.isBlank(s)) {
			return null;
		}
		if (StringUtils.isBlank(separatorChars)) {
			return null;
		}

		String[] ss = StringUtils.split(s, separatorChars);
		if (ss == null || ss.length == 0) {
			return null;
		}

		List<String> list = new LinkedList<String>();
		for (String tem : ss) {
			if (StringUtils.isNotBlank(tem) && !list.contains(tem)) {
				list.add(tem.trim());
			}
		}

		return list;
	}

	/** 将两个Set合并 */
	public static Set<String> union(Set<String> set1, Set<String> set2) {
		if (isEmpty(set1)) {
			return set2;
		}
		if (isEmpty(set2)) {
			return set1;
		}

		Set<String> back = new HashSet<String>();
		back.addAll(set1);
		back.addAll(set2);

		return back;
	}

	// ----------------------------------------------------------------------------------------------
	/**
	 * 根据关键字加载提示信息转换为Set<String>，默认分隔符为半角逗号</br> 配置格式为appid1,appid2,appid3</br>
	 *
	 * @return
	 */
	public static Set<String> deserialize2Set(String context) {
		// 根据分隔符获取数据
		return deserialize2Set(context, ",");
	}

	/**
	 * 根据关键字加载提示信息</br> 配置格式为appid1,appid2,appid3</br>
	 *
	 * @return
	 */
	public static Set<String> deserialize2Set(String context, String separator) {
		Set<String> back = new HashSet<String>();

		if (StringUtils.isBlank(context)) {
			return back;
		}
		if (StringUtils.isBlank(separator)) {
			return back;
		}

		// 根据分隔符获取数据
		String[] tokenLimitArray = context.split(separator);

		if (tokenLimitArray.length > 0) {
			for (String limt : tokenLimitArray) {
				if (StringUtils.isNotBlank(limt)) {
					back.add(limt);
				}
			}
		}
		return back;
	}

	// ----------------------------------------------------------------------------------------------
	/**
	 * 将Collection转换成String,默认分隔符为半角逗号</br>
	 * 
	 * @param list
	 * @return
	 */
	public static String collection2String(Collection<String> list) {
		return collection2String(list, ",");
	}

	public static String collection2String(Collection<String> list, String separator) {
		if (list == null || list.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String tem : list) {
			if (true == first) {
				first = false;
			} else {
				sb.append(separator);
			}

			sb.append(tem);
		}

		return sb.toString();
	}

	/**
	 * 为空
	 * @author： wangyi
	 * @date： 2017年8月21日 上午11:15:40 
	 * @param c
	 * @return
	 */
	public static boolean isEmpty(Collection c) {
		if (c == null || c.isEmpty()) {
			return true;
		}

		return false;
	}

	/**
	 * 不为空
	 * @author： wangyi
	 * @date： 2017年8月21日 上午11:15:40 
	 * @param c
	 * @return
	 */
	public static boolean isNotEmpty(Collection c) {
		return !isEmpty(c);
	}

	/**
	 * 集合判断
	 * @author： wangyi
	 * @date： 2017年8月21日 上午11:16:37 
	 * @param c
	 * @return
	 */
	public static boolean isEmpty(Map c) {
		if (c == null || c.isEmpty()) {
			return true;
		}

		return false;
	}

	/**
	 * 集合判断
	 * @author： wangyi
	 * @date： 2017年8月21日 上午11:16:41 
	 * @param c
	 * @return
	 */
	public static boolean isNotEmpty(Map c) {
		return !isEmpty(c);
	}

	/**
	 * 集合判断
	 * @author： wangyi
	 * @date： 2017年8月21日 上午11:16:46 
	 * @param objs
	 * @return
	 */
	public static boolean isEmpty(Object[] objs) {
		if (objs == null || objs.length == 0) {
			return true;
		}

		return false;
	}

	/**
	 * 集合判断
	 * @author： wangyi
	 * @date： 2017年8月21日 上午11:16:52 
	 * @param objs
	 * @return
	 */
	public static boolean isNotEmpty(Object[] objs) {
		return !isEmpty(objs);
	}

	/**
	 * 集合判断
	 * @author： wangyi
	 * @date： 2017年8月21日 上午11:16:57 
	 * @param bs
	 * @return
	 */
	public static boolean isEmpty(byte[] bs) {
		if (bs == null || bs.length == 0) {
			return true;
		}

		return false;
	}

	public static boolean isNotEmpty(byte[] bs) {
		return !isEmpty(bs);
	}

	
}
