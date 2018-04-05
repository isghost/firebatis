package com.ccyblog.firebaits.po;

import java.util.Date;
import lombok.Data;

/**
 * @author isghost
 * @date 2017/12/19.
 * @desc
 */
@Data
public class BasePO {
    /**
     * id
     */
    private Long id;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 乐观锁的更新版本
     */
    private int version;

}
