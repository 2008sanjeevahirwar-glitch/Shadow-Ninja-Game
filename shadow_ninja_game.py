import pygame
import random
import math

# Initialize Pygame
pygame.init()

# Screen dimensions
SCREEN_WIDTH = 800
SCREEN_HEIGHT = 600
screen = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT))
pygame.display.set_caption("Shadow Ninja: Trials of the Dojo")

# Colors
BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
RED = (255, 0, 0)
GREEN = (0, 255, 0)
BLUE = (0, 0, 255)
GRAY = (128, 128, 128)

# Clock for FPS
clock = pygame.time.Clock()
FPS = 60

# Player class
class Player(pygame.sprite.Sprite):
    def __init__(self):
        super().__init__()
        self.image = pygame.Surface((40, 60))
        self.image.fill(BLUE)
        self.rect = self.image.get_rect()
        self.rect.x = 100
        self.rect.y = 400
        self.vel_x = 0
        self.vel_y = 0
        self.speed = 5
        self.jump_power = -15
        self.gravity = 0.8
        self.on_ground = False
        self.double_jump = True
        self.dash_cooldown = 0
        self.dash_speed = 15
        self.wall_jumping = False
        self.stealth = False
        self.health = 100
        self.coins = 0
        self.skills = {"double_jump": False, "dash": False}

    def update(self, platforms, enemies):
        keys = pygame.key.get_pressed()
        self.vel_x = 0
        if keys[pygame.K_LEFT] or keys[pygame.K_a]:
            self.vel_x = -self.speed
        if keys[pygame.K_RIGHT] or keys[pygame.K_d]:
            self.vel_x = self.speed
        if keys[pygame.K_SPACE] and self.on_ground:
            self.vel_y = self.jump_power
            self.on_ground = False
        elif keys[pygame.K_SPACE] and self.double_jump and not self.on_ground:
            self.vel_y = self.jump_power
            self.double_jump = False
        if keys[pygame.K_LSHIFT] and self.dash_cooldown == 0 and self.skills["dash"]:
            if keys[pygame.K_LEFT] or keys[pygame.K_a]:
                self.vel_x = -self.dash_speed
            elif keys[pygame.K_RIGHT] or keys[pygame.K_d]:
                self.vel_x = self.dash_speed
            self.dash_cooldown = 30
        if keys[pygame.K_s]:
            self.stealth = True
            self.image.set_alpha(100)
        else:
            self.stealth = False
            self.image.set_alpha(255)
        self.vel_y += self.gravity
        for platform in platforms:
            if self.rect.colliderect(platform.rect) and self.vel_x != 0:
                if self.vel_y > 0:
                    self.vel_y = 2
                    if keys[pygame.K_SPACE]:
                        self.vel_y = self.jump_power
                        self.vel_x = -self.vel_x
                        self.wall_jumping = True
        self.rect.x += self.vel_x
        self.rect.y += self.vel_y
        self.on_ground = False
        for platform in platforms:
            if self.rect.colliderect(platform.rect):
                if self.vel_y > 0:
                    self.rect.bottom = platform.rect.top
                    self.vel_y = 0
                    self.on_ground = True
                    self.double_jump = True
                    self.wall_jumping = False
                elif self.vel_y < 0:
                    self.rect.top = platform.rect.bottom
                    self.vel_y = 0
        if self.dash_cooldown > 0:
            self.dash_cooldown -= 1
        if keys[pygame.K_f]:
            for enemy in enemies:
                if self.rect.colliderect(enemy.rect):
                    enemy.health -= 20
        for coin in coins:
            if self.rect.colliderect(coin.rect):
                self.coins += 1
                coins.remove(coin)

class Enemy(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.Surface((40, 60))
        self.image.fill(RED)
        self.rect = self.image.get_rect()
        self.rect.x = x
        self.rect.y = y
        self.health = 50
        self.vision_cone = 100
        self.alerted = False

    def update(self, player):
        if abs(self.rect.x - player.rect.x) < self.vision_cone and not player.stealth:
            self.alerted = True
            if self.rect.x < player.rect.x:
                self.rect.x += 2
            else:
                self.rect.x -= 2
        else:
            self.alerted = False
            self.rect.x += random.choice([-1, 1])
        if self.health <= 0:
            self.kill()

class Platform(pygame.sprite.Sprite):
    def __init__(self, x, y, width, height):
        super().__init__()
        self.image = pygame.Surface((width, height))
        self.image.fill(GRAY)
        self.rect = self.image.get_rect()
        self.rect.x = x
        self.rect.y = y

class Trap(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.Surface((40, 20))
        self.image.fill(RED)
        self.rect = self.image.get_rect()
        self.rect.x = x
        self.rect.y = y

    def update(self, player):
        if self.rect.colliderect(player.rect):
            player.health -= 10

class Coin(pygame.sprite.Sprite):
    def __init__(self, x, y):
        super().__init__()
        self.image = pygame.Surface((20, 20))
        self.image.fill(GREEN)
        self.rect = self.image.get_rect()
        self.rect.x = x
        self.rect.y = y

all_sprites = pygame.sprite.Group()
platforms = pygame.sprite.Group()
enemies = pygame.sprite.Group()
traps = pygame.sprite.Group()
coins = pygame.sprite.Group()

player = Player()
all_sprites.add(player)

platform1 = Platform(0, 500, 800, 20)
platform2 = Platform(200, 400, 200, 20)
platform3 = Platform(500, 300, 200, 20)
platforms.add(platform1, platform2, platform3)
all_sprites.add(platform1, platform2, platform3)

enemy1 = Enemy(300, 450)
enemy2 = Enemy(600, 250)
enemies.add(enemy1, enemy2)
all_sprites.add(enemy1, enemy2)

trap1 = Trap(250, 480)
traps.add(trap1)
all_sprites.add(trap1)

coin1 = Coin(220, 380)
coin2 = Coin(520, 280)
coins.add(coin1, coin2)
all_sprites.add(coin1, coin2)

running = True
while running:
    clock.tick(FPS)
    screen.fill(BLACK)

    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False

    player.update(platforms, enemies)
    enemies.update(player)
    traps.update(player)

    all_sprites.draw(screen)

    font = pygame.font.SysFont(None, 24)
    health_text = font.render(f"Health: {player.health}", True, WHITE)
    coins_text = font.render(f"Coins: {player.coins}", True, WHITE)
    screen.blit(health_text, (10, 10))
    screen.blit(coins_text, (10, 40))

    if player.coins >= 2 and not player.skills["dash"]:
        player.skills["dash"] = True
        print("Dash unlocked!")

    pygame.display.flip()

pygame.quit()
