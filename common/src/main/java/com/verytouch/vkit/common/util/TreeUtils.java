package com.verytouch.vkit.common.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 无限分类工具类
 *
 * @author verytouch
 * @since 2020/10/3 17:40
 */
public class TreeUtils {

	/**
	 * 链表转树
	 * @param list 链表结构数据，分类过程中，其中的元素会逐渐remove掉
	 * @param rootId 根分类ID，不能由rootID寻找到的元素会遗留在items里
     * @return 树结构数据
	 */
	public static List<? extends TreeAble> listToTree(List<? extends TreeAble> list, Object rootId) {
		List<TreeAble> tree = new LinkedList<>();
		if (list == null || list.isEmpty() || rootId == null) {
			return tree;
		}
		Iterator<? extends TreeAble> iterator = list.iterator();
		while (iterator.hasNext()) {
			TreeAble item = iterator.next();
			if (rootId.equals(item.pid())) {
				tree.add(item);
				iterator.remove();
			}
		}
		for (TreeAble node : tree) {
			node.children(listToTree(list, node.id()));
		}
		return tree;
	}

    /**
     * 树转成链表
     * @param tree 树结构元素，转换过程中，原本的children会被置null
     * @return 链表结构数据
     */
	public static List<? extends TreeAble> treeToList(List<? extends TreeAble> tree) {
	    List<TreeAble> list = new LinkedList<>();
        if (tree == null || tree.isEmpty()) {
            return list;
        }
        for (TreeAble node : tree) {
            list.add(node);
            if (node.children() != null) {
                list.addAll(treeToList(node.children()));
            }
            node.children(null);
        }
	    return list;
    }

    /**
     * 查找子树
     * @param tree 树
     * @param id 子树父节点ID
     * @return 子树
     */
    public static TreeAble getChildTree(List<? extends TreeAble> tree, Object id) {
        if (tree == null || tree.isEmpty()) {
            return null;
        }
        for (TreeAble node : tree) {
            if (Objects.equals(node.id(), id)) {
                return node;
            }
            TreeAble childTree = getChildTree(node.children(), id);
            if (childTree != null) {
                return childTree;
            }
        }
        return null;
    }
	
	/**
	 * 待分类实体需要实现的接口
	 * 不以get/set开头以防干扰序列化/反序列化
	 */
	public static interface TreeAble {
		Object id();
		Object pid();
		void children(List<? extends TreeAble> children);
		List<? extends TreeAble> children();
	}
}


