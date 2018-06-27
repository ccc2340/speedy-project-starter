package org.speedy.data.orm.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.speedy.data.orm.domain.sql.PageResult;
import org.speedy.data.orm.domain.sql.SqlQueryParameter;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/12 10:53
 */
public interface RepositoryTemplate {

    /* 保存新对象，返回的对象为保存之后的对象，其中包含主键 */
    <T> int insert(T t);

    <T> int batchInsert(T[] ts);

    /* 根据主键删除数据 */
    <T> int deleteByPrimary(Class<T> clazz, Serializable primary);

    /* 以主键为条件，修改对象的其他数据 */
    <T> int updateByPrimary(T t);

    /* 以主键为条件，查询符合条件的对象 */
    <T> Optional<T> selectByPrimary(Class<T> clazz, Serializable primary);

    /* 以多个主键为条件，查询符合条件的多个对象 */
    <T> List<T> selectByPrimaries(Class<T> clazz, List<Serializable> primaries);

    /* 以指定对象为条件，查询符合条件的对象集合 */
    <T> List<T> selectForList(T example);

    <T> List<T> selectForClass(Class<T> clazz);

    /* 分页数据查询 */
    PageResult selectForPage(SqlQueryParameter queryParameter);

}
