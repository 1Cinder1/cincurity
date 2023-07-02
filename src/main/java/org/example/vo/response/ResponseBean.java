package org.example.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.constants.ResultCode;

/**
 * @author : zhangxiaobo
 * @version : v1.0
 * @description : 请求的返回Json对应的Bean的模板类
 * @createTime : 2023/4/17 8:03
 * @updateUser : zhangxiaobo
 * @updateTime : 2023/4/17 8:03
 * @updateRemark :
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class
ResponseBean<T> {
    /**
     * 实现功能:是否成功
     */
    private Integer success;

    /**
     * 实现功能：返回代码
     */
    protected Long code;

    /**
     * 实现功能：返回信息
     */
    protected String message;

    /**
     * 返回数据结构
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected T data;


    /**
     * 实现功能：常用成功的返回，带额外数据
     *
     * @param data 当前数据
     * @return 请求成功ResponseBean
     */
    public static <TData> ResponseBean<TData> success(TData data) {
        return new ResponseBean<>(1, ResultCode.SUCCESS, data);
    }

    /**
     * 实现功能：常用成功的返回，不带额外数据
     *
     * @return 请求成功ResponseBean
     */
    public static <TData> ResponseBean<TData> success() {
        return success(null);
    }

    /**
     * 实现功能:常用失败的运回
     *
     * @param code    出错代码
     * @param message 出错信息
     * @return 请求失败ResponseBean
     */
    public static ResponseBean fail(Long code, String message) {
        return new ResponseBean(0, code, message, null);
    }

    /**
     * 实现功能:常用失败的运回
     *
     * @param resultCode 出错代码枚举类型
     * @param message 出错信息
     * @return 请求失败ResponseBean
     */
    public static ResponseBean fail(ResultCode resultCode, String message) {
        return fail(resultCode.getCode(), message);
    }
    /**
     * 实现功能:常用失败的运回
     *
     * @param resultCode ResultCode枚举值
     * @return 请求失败ResponseBean
     */
    public static ResponseBean fail(ResultCode resultCode) {
        return fail(resultCode.getCode(), resultCode.getMsg());
    }
    public static ResponseBean fail(String message) {
        return fail(ResultCode.ERROR, message);
    }

    /**
     * 构造方法
     * @param success 是否成功 0：失败 1：成功
     * @param resultCode 状态码
     * @param data 携带数据
     */
    public ResponseBean(Integer success, ResultCode resultCode, T data) {
        this.success = success;
        this.code = resultCode.getCode();
        this.message = resultCode.getMsg();
        this.data = data;
    }

    public ResponseBean(T data) {
        this(ResultCode.SUCCESS,data);
    }

    public ResponseBean(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMsg();
        this.data = data;
    }

    public ResponseBean(ResultCode resultCode, String msg) {
        this.code = resultCode.getCode();
        this.message = msg;
    }

    public ResponseBean(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMsg();
        this.data = null;
    }

}
