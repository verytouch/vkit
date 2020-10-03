package com.verytouch.vkit.common.util;

import lombok.Data;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TreeUtilsTest {

    @Test
    public void testToTree() {
        List<Tag> list = Stream.of(
            new Tag(1, 0, "电器"),
            new Tag(11, 1, "电视"),
            new Tag(12, 1, "冰箱"),

            new Tag(2, 0, "书籍"),
            new Tag(21, 2, "历史"),
            new Tag(22, 2, "文化"),
            new Tag(221, 22, "诗词"),
            new Tag(222, 22, "歌赋"),
            new Tag(2211, 221, "李白"),
            new Tag(2212, 221, "杜甫")
        ).collect(Collectors.toList());

        System.out.println(list.size());
        List<? extends TreeUtils.TreeAble> tree = TreeUtils.listToTree(list, 0);
        System.out.println(tree);

        System.out.println(TreeUtils.getChildTree(tree, 221));

        List<Tag> treeAbles = (List<Tag>) TreeUtils.treeToList(tree);
        System.out.println(treeAbles);
    }

    @Data
    static class Tag implements TreeUtils.TreeAble {

        private Integer id;
        private Integer pid;
        private String name;
        private List<Tag> children;

        public Tag(Integer id, Integer pid, String name) {
            this.id = id;
            this.pid = pid;
            this.name = name;
        }

        @Override
        public Object id() {
            return id;
        }

        @Override
        public Object pid() {
            return pid;
        }

        @Override
        public void children(List<? extends TreeUtils.TreeAble> children) {
            this.children = (List<Tag>) children;
        }

        @Override
        public List<? extends TreeUtils.TreeAble> children() {
            return children;
        }
    }
}