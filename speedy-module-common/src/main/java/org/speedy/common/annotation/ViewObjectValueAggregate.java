package org.speedy.common.annotation;

import lombok.Data;
import org.speedy.common.constant.FontColor;

import java.util.Collection;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/28 08:56
 */
@Data
public class ViewObjectValueAggregate {

    private Object content;
    private String color;
    private String js;
    private boolean button;

    public static ViewObjectValueAggregate createToButton(Collection<ViewObjectButton> buttons) {
        ViewObjectValueAggregate aggregate = new ViewObjectValueAggregate();
        aggregate.button = true;
        aggregate.content = buttons;
        aggregate.color = FontColor.BLACK.getColorCode();
        return aggregate;
    }

    public static ViewObjectValueAggregate createToNonButton(String colorCode, Object content, String href) {
        ViewObjectValueAggregate aggregate = new ViewObjectValueAggregate();
        aggregate.button = false;
        aggregate.content = content;
        aggregate.color = colorCode;
        aggregate.js = href;
        return aggregate;
    }
}
