package org.speedy.common.annotation;

import lombok.Getter;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/28 08:54
 */
@Getter
public class ViewObjectButton {

    private String showText;
    private String methodName;
    private String methodHref;
    private String className;

    public ViewObjectButton() {
    }

    public static ViewObjectButton create(ButtonType type, Object primary, int index) {
        ViewObjectButton button = new ViewObjectButton();
        button.showText = type.getShowText();
        button.className = type.getClassName();
        button.methodName = type.name().toLowerCase();
        button.methodHref = String.format("%s('%s','%s')", button.methodName, primary, index);
        return button;
    }

    /* 详情按钮 */
    public static ViewObjectButton detail() {
        return new ViewObjectButton();
    }

    /* 编辑按钮 */
    public static ViewObjectButton edit() {
        return new ViewObjectButton();
    }
}
