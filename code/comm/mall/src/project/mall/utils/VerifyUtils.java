package project.mall.utils;

import kernel.exception.BusinessException;

/**
 * 验证工具
 */
public class VerifyUtils {
    /**
     * 校验是否所有参数都非空（null、空串、空白都认为是空）
     * 任意一个为空则返回 true
     */
    public static boolean hasEmpty(Object... args) {
        for (Object arg : args) {
            if (arg == null) {
                return true;
            }
            if (arg instanceof String && ((String) arg).trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验是否所有参数都非空（null、空串、空白都认为是空）
     * 任意一个为空则报错
     */
    public static void hasEmptyError(Object... args) {
        for (Object arg : args) {
            if (arg == null) {
                throw new BusinessException("缺少必要参数");
            }
            if (arg instanceof String && ((String) arg).trim().isEmpty()) {
                throw new BusinessException("缺少必要参数");
            }
        }
    }

    /**
     * 长度2-50 店铺名验证
     */
    public static void validSellerNameError(String input) {
        if (input == null || input.length() < 2 || input.length() > 50) {
            throw new BusinessException("店铺名格式不正确，长度必须为2-50");
        }
    }

    /**
     * 邮箱验证
     */
    public static void validEmailError(String input) {
        if (!(input != null && input.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))) {
            throw new BusinessException("邮箱格式不正确");
        }
    }

    // 密码校验：6-18位，允许字母、数字、下划线等常规字符
    public static void validPasswordError(String password) {
        if (!(password != null && password.matches("^[\\w!@#$%^&*()_+=-]{6,12}$"))) {
            throw new IllegalArgumentException("密码格式不正确,长度6-12位,允许字母、数字、下划线等常规字符");
        }
    }

    // 资金密码校验：必须是6位纯数字
    public static void validFundPasswordError(String fundPassword) {
        if (!(fundPassword != null && fundPassword.matches("^\\d{6}$"))) {
            throw new IllegalArgumentException("资金密码格式不正确,必须是六位数字");
        }
    }
}
