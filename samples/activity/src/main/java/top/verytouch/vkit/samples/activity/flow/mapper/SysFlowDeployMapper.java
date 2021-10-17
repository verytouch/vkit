package top.verytouch.vkit.samples.activity.flow.mapper;

import top.verytouch.vkit.samples.activity.flow.entity.SysFlowDeploy;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * @author groovy
 * @since 2021-10-17 12:46:00
 */
@Repository
public interface SysFlowDeployMapper {

    int insert(SysFlowDeploy entity);

    int insertBatch(@Param("list") List<SysFlowDeploy> list);

    int deleteById(Integer id);

    int updateById(SysFlowDeploy entity);

    SysFlowDeploy getById(Integer id);

    List<SysFlowDeploy> getList(SysFlowDeploy entity);
}
