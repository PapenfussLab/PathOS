
            easygrid{
               defaults{

                //un-comment if you use spring security or implement your own with your framework
                securityProvider = { grid, oper ->
                    return true
/*
                    if (!grid.roles) {
                        return true
                    }
                    def grantedRoles
                    if (Map.isAssignableFrom(grid.roles.getClass())) {
                        grantedRoles = grid.roles.findAll { op, role -> oper == op }.collect { op, role -> role }
                    } else if (List.isAssignableFrom(grid.roles.getClass())) {
                        grantedRoles = grid.roles
                    } else {
                        grantedRoles = [grid.roles]
                    }
                    SpringSecurityUtils.ifAllGranted(grantedRoles.join(','))
*/
                }

               }
            }
            