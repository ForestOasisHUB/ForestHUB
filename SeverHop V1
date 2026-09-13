-- ========================================================
-- 🌿 FOREST OASIS • ULTIMATE 1-PLAYER SERVER HOPPER 🌿
-- ========================================================
local HttpService = game:GetService("HttpService")
local TeleportService = game:GetService("TeleportService")
local Players = game:GetService("Players")
local LocalPlayer = Players.LocalPlayer

local PlaceId = game.PlaceId

local function NotifyVIP(titleText, descText)
    pcall(function()
        game:GetService("StarterGui"):SetCore("SendNotification", {
            Title = "🌿 [FOREST OASIS] • " .. titleText,
            Text = descText,
            Duration = 3.5
        })
    end)
end

local function UltimateOnePlayerHop()
    NotifyVIP("Server Hopper", "⚡ Scanning system for an empty 1-player server...")
    
    local cursor = ""
    local validServers = {}
    
    -- Fetch server list from Roblox API
    repeat
        local url = "https://games.roblox.com/v1/games/" .. PlaceId .. "/servers/Public?sortOrder=Asc&limit=100"
        if cursor and cursor ~= "" then
            url = url .. "&cursor=" .. cursor
        end
        
        local success, result = pcall(function()
            return HttpService:JSONDecode(game:HttpGet(url))
        end)
        
        if success and result and result.data then
            for _, server in ipairs(result.data) do
                -- Strict filter: target servers with exactly 1 player
                if server.id ~= game.JobId and server.playing == 1 and server.maxPlayers and server.playing < server.maxPlayers then
                    table.insert(validServers, server)
                end
            end
            cursor = result.nextPageCursor
        else
            break
        end
    until not cursor or cursor == "" or #validServers >= 10

    if #validServers > 0 then
        local targetServer = validServers[math.random(1, #validServers)]
        
        NotifyVIP("Success! 🎯", "🚀 Found a 1-player server. ForestOasis is teleporting you to your private world...")
        
        pcall(function()
            TeleportService:TeleportToPlaceInstance(PlaceId, targetServer.id, LocalPlayer)
        end)
    else
        NotifyVIP("Searching...", "⏳ No 1-player server found yet, ForestOasis is scanning the next page...")
        task.wait(1.5)
        UltimateOnePlayerHop()
    end
end

-- Run the script
UltimateOnePlayerHop()
