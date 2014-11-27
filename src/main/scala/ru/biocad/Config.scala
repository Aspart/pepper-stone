package my.com

/**
 * Created by roman on 15/10/14.
 */
case class Config(in: String = "",
                  out: String = "",
                  header: Boolean = false,
                  verbose: Boolean = false,
                  debug: Boolean = false,
                  split: Boolean = false)