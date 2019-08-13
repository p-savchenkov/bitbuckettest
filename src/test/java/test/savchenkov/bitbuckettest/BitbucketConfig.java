package test.savchenkov.bitbuckettest;

import org.aeonbits.owner.Config;

public interface BitbucketConfig extends Config{

  @DefaultValue("https://api.bitbucket.org/")
  String apiHost();

  @DefaultValue("p-savchenkov")
  String userName();

  @DefaultValue("EH8W4HRZuhfKsU7WKcY3")
  String password();

  @DefaultValue("bitbuckettest")
  String repo();
}