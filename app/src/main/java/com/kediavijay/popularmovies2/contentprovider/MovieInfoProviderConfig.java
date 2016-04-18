package com.kediavijay.popularmovies2.contentprovider;

import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;

/**
 * Created by vijaykedia on 17/04/16.
 */
@SimpleSQLConfig(
        name = "MovieInfoProvider",
        authority = "com.kediavijay.popularmovies2.contentprovider.MovieInfoProvider",
        database = "movie_info.db",
        version = 1
)
public class MovieInfoProviderConfig implements ProviderConfig {

    @Override
    public UpgradeScript[] getUpdateScripts() {
        return new UpgradeScript[0];
    }
}
