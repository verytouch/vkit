package top.verytouch.vkit.samples.activity.flow.mapper;

import top.verytouch.vkit.samples.activity.flow.entity.SysFlow;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * @author groovy
 * @since 2021-10-17 12:46:00
 */
@Repository
public interface SysFlowMapper {

    int insert(SysFlow entity);

    int insertBatch(@Param("list") List<SysFlow> list);

    int deleteById(String id);

    int updateById(SysFlow entity);

    SysFlow getById(String id);

    List<SysFlow> getList(SysFlow entity);

}
