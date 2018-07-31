package org.speedy.common.annotation;

import lombok.Getter;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/28 14:18
 */
@Getter
public enum ButtonType {

    DETAIL("详情", "layui-btn layui-btn-xs"),
    EDIT("编辑", "layui-btn layui-btn-xs"),
    PREVIEW("预览", "layui-btn layui-btn-xs layui-btn-normal"),
    IMAGE_EDIT("图片", "layui-btn layui-btn-xs layui-btn-warm"),

    /* 库存相关 */
    INCREASE("加仓", "layui-btn layui-btn-xs layui-btn-normal"),
    DECREASE("减仓", "layui-btn layui-btn-xs layui-btn-warm"),

    /* 房型相关 */
    IMAGE("图片", "layui-btn layui-btn-xs layui-btn-warm"),

    /* 订单相关 */
    SUPPORT("保障", "layui-btn layui-btn-xs"),

    //
    ;
    private final String showText;
    private final String className;

    ButtonType(String showText, String className) {
        this.showText = showText;
        this.className = className;
    }
}
