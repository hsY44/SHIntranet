package com.sh.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.sh.dto.WorkSubmitDTO;

@FunctionalInterface
public interface JFunction
{
	WorkSubmitDTO getDto(ResultSet rs) throws SQLException;
}
