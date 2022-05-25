package net.tenie.plugin.DataModel.po;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface DataModelInfoMapper {
	@Select("SELECT * FROM DATA_MODEL_INFO WHERE id = #{id}")
	DataModelInfoPo selectDataModelInfo(int id);
}
