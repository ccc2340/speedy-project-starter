package org.speedy.common.constant;

import lombok.Getter;
import org.speedy.common.domain.Color;

/**
 * @Description 字体颜色，用于设置页面显示
 * @Author chenguangxue
 * @CreateDate 2018/06/21 15:39
 */
@Getter
public enum FontColor implements Color {

    BLACK("black"), WHITE("white"), GREEN("green"), RED("red"), BLUE("blue")
    // end
    ;

    private final String colorCode;

    FontColor(String colorCode) {
        this.colorCode = colorCode;
    }
}
