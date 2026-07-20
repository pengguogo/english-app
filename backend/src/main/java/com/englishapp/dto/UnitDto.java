package com.englishapp.dto;

/**
 * 单元 DTO(带进度信息)
 * <p>
 * 用于向前端返回单元基本信息以及该单元下课程的进度统计,
 * 包括课程总数与已完成课程数。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
public class UnitDto {

    /** 单元 ID */
    private final Integer id;

    /** 单元名称 */
    private final String name;

    /** 排序序号 */
    private final Integer sortOrder;

    /** 是否锁定 */
    private final Boolean isLocked;

    /** 单元下课程总数 */
    private final Integer totalLessons;

    /** 单元下已完成课程数 */
    private final Integer completedLessons;

    /**
     * 构造单元 DTO
     *
     * @param id               单元 ID
     * @param name             单元名称
     * @param sortOrder        排序序号
     * @param isLocked         是否锁定
     * @param totalLessons     课程总数
     * @param completedLessons 已完成课程数
     */
    public UnitDto(Integer id, String name, Integer sortOrder, Boolean isLocked,
                   Integer totalLessons, Integer completedLessons) {
        this.id = id;
        this.name = name;
        this.sortOrder = sortOrder;
        this.isLocked = isLocked;
        this.totalLessons = totalLessons;
        this.completedLessons = completedLessons;
    }

    /**
     * 获取单元 ID
     *
     * @return 单元 ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 获取单元名称
     *
     * @return 单元名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取排序序号
     *
     * @return 排序序号
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * 获取是否锁定
     *
     * @return 是否锁定
     */
    public Boolean getIsLocked() {
        return isLocked;
    }

    /**
     * 获取课程总数
     *
     * @return 课程总数
     */
    public Integer getTotalLessons() {
        return totalLessons;
    }

    /**
     * 获取已完成课程数
     *
     * @return 已完成课程数
     */
    public Integer getCompletedLessons() {
        return completedLessons;
    }
}
